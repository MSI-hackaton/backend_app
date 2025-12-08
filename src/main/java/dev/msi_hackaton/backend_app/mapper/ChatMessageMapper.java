package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.ChatMessage;
import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toEntity(ChatMessageCreateDto dto) {
        ChatMessage entity = new ChatMessage();
        entity.setMessage(dto.getMessage());
        return entity;
    }

    public ChatMessageResponseDto toDto(ChatMessage message) {
        ChatMessageResponseDto dto = new ChatMessageResponseDto();
        dto.setId(message.getId());
        dto.setRequestId(message.getRequest().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setMessage(message.getMessage());
        dto.setSentAt(message.getCreatedAt());
        dto.setIsRead(message.getIsRead());
        return dto;
    }
}