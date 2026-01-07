package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ChatMessage;
import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.repository.ChatMessageRepository;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConstructionStageRepository constructionStageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessageResponseDto sendMessage(UUID constructionId, UUID senderId, ChatMessageCreateDto createDto) {
        ConstructionStage construction = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + constructionId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + senderId));

        // Создаем сообщение через конструктор/сеттеры
        ChatMessage message = new ChatMessage();
        message.setConstruction(construction);
        message.setSender(sender);
        message.setMessage(createDto.getMessage());
        message.setIsRead(false);

        ChatMessage saved = chatMessageRepository.save(message);

        // Создаем DTO для ответа
        ChatMessageResponseDto responseDto = new ChatMessageResponseDto();
        responseDto.setId(saved.getId());
        responseDto.setConstructionId(constructionId);
        responseDto.setSenderId(senderId);
        responseDto.setSenderName(sender.getFullName());
        responseDto.setMessage(saved.getMessage());
        responseDto.setIsRead(saved.getIsRead());
        responseDto.setCreatedAt(saved.getCreatedAt());

        // Отправляем через WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + constructionId,
                responseDto
        );

        return responseDto;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getChatHistory(UUID constructionId) {
        return chatMessageRepository.findByConstructionIdAndIsReadFalse(constructionId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void markAsRead(UUID messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));

        message.setIsRead(true);
        chatMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(UUID constructionId, UUID userId) {
        return chatMessageRepository.countByConstructionIdAndIsReadFalse(constructionId);
    }

    private ChatMessageResponseDto mapToDto(ChatMessage message) {
        ChatMessageResponseDto dto = new ChatMessageResponseDto();
        dto.setId(message.getId());
        dto.setConstructionId(message.getConstruction().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setMessage(message.getMessage());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}