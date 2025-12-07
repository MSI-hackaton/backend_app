package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dto.request.ChatMessageRequest;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponse;
import dev.msi_hackaton.backend_app.entity.ChatMessage;
import dev.msi_hackaton.backend_app.entity.Order;
import dev.msi_hackaton.backend_app.entity.User;
import dev.msi_hackaton.backend_app.repository.ChatMessageRepository;
import dev.msi_hackaton.backend_app.repository.OrderRepository;
import dev.msi_hackaton.backend_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       OrderRepository orderRepository,
                       UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, Long senderId) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ChatMessage message = new ChatMessage();
        message.setOrder(order);
        message.setSender(sender);
        message.setMessage(request.getMessage());
        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);

        ChatMessage saved = chatMessageRepository.save(message);
        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getOrderMessages(Long orderId) {
        List<ChatMessage> messages = chatMessageRepository.findByOrder_IdOrderBySentAt(orderId);
        return messages.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void markMessagesAsRead(Long orderId, Long userId) {

        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByOrder_IdAndIsReadFalse(orderId);

        for (ChatMessage message : unreadMessages) {
            if (!message.getSender().getId().equals(userId)) {
                message.setIsRead(true);
            }
        }

        chatMessageRepository.saveAll(unreadMessages);
    }

    @Transactional(readOnly = true)
    public Long getUnreadCount(Long orderId, Long userId) {
        return chatMessageRepository.countByOrder_IdAndIsReadFalse(orderId);
    }

    private ChatMessageResponse convertToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setOrderId(message.getOrder().getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getFullName());
        response.setMessage(message.getMessage());
        response.setAttachmentUrl(message.getAttachmentUrl());
        response.setIsRead(message.getIsRead());
        response.setSentAt(message.getSentAt());
        return response;
    }
}