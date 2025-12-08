package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ChatMessage;
import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.repository.ChatMessageRepository;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionRequestRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ConstructionRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       ConstructionRequestRepository requestRepository,
                       UserRepository userRepository,
                       ChatMessageMapper chatMessageMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.chatMessageMapper = chatMessageMapper;
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

        // Временное решение - берем первого пользователя как отправителя
        User sender = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No users found"));

        ChatMessage message = chatMessageMapper.toEntity(messageDto);
        message.setRequest(request);
        message.setSender(sender);
        message.setIsRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);
        return chatMessageMapper.toDto(savedMessage);
    }

    @Transactional
    public void markAsRead(UUID messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found: " + messageId));
        message.setIsRead(true);
        chatMessageRepository.save(message);
    }
}