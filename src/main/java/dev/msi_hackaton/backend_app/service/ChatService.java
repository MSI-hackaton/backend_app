package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ChatMessage;
import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.repository.ChatMessageRepository;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionRequestRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import dev.msi_hackaton.backend_app.dto.websocket.WebSocketMessageDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.ChatMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final ConstructionRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            ConstructionRequestRepository requestRepository,
            UserRepository userRepository,
            ChatMessageMapper chatMessageMapper,
            SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.chatMessageMapper = chatMessageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessages(UUID requestId) {
        List<ChatMessage> messages = chatMessageRepository.findByRequestIdOrderByCreatedAt(requestId);
        return messages.stream()
                .map(chatMessageMapper::toDto)
                .toList();
    }

    @Transactional
    public ChatMessageResponseDto sendMessage(UUID requestId, ChatMessageCreateDto messageDto) {
        ConstructionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found: " + requestId));

        // TODO: В реальном приложении получать пользователя из SecurityContext
        // Сейчас берем первого пользователя для теста
        User sender = userRepository.findAll().stream()
                .filter(u -> u.getRole().name().equals("CUSTOMER") || u.getRole().name().equals("SPECIALIST"))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No users found"));

        ChatMessage message = chatMessageMapper.toEntity(messageDto);
        message.setRequest(request);
        message.setSender(sender);
        message.setIsRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageResponseDto responseDto = chatMessageMapper.toDto(savedMessage);

        // Отправляем уведомление через WebSocket
        sendWebSocketNotification(requestId, responseDto);

        return responseDto;
    }

    /**
     * Отправка уведомления через WebSocket
     */
    private void sendWebSocketNotification(UUID requestId, ChatMessageResponseDto messageDto) {
        try {
            WebSocketMessageDto wsMessage = new WebSocketMessageDto();
            wsMessage.setType("MESSAGE");
            wsMessage.setMessageId(messageDto.getId());
            wsMessage.setRequestId(requestId);
            wsMessage.setSenderId(messageDto.getSenderId());
            wsMessage.setSenderName(messageDto.getSenderName());
            wsMessage.setMessage(messageDto.getMessage());
            wsMessage.setTimestamp(Instant.now());
            wsMessage.setIsRead(false);

            // Отправляем в тему для этой заявки
            messagingTemplate.convertAndSend("/topic/chat/" + requestId, wsMessage);

            log.info("WebSocket уведомление отправлено для заявки {}", requestId);

        } catch (Exception e) {
            log.error("Ошибка отправки WebSocket уведомления: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void markAsRead(UUID messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));
        message.setIsRead(true);
        chatMessageRepository.save(message);

        // Отправляем уведомление о прочтении
        sendReadNotification(message);
    }

    private void sendReadNotification(ChatMessage message) {
        try {
            WebSocketMessageDto notification = new WebSocketMessageDto();
            notification.setType("READ");
            notification.setMessageId(message.getId());
            notification.setRequestId(message.getRequest().getId());
            notification.setTimestamp(Instant.now());

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + message.getRequest().getId() + "/read",
                    notification
            );
        } catch (Exception e) {
            log.error("Ошибка отправки уведомления о прочтении: {}", e.getMessage());
        }
    }
}