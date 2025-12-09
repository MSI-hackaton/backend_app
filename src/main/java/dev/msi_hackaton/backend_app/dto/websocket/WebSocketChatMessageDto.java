package dev.msi_hackaton.backend_app.dto.websocket;

import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class WebSocketChatMessageDto {
    // Тип сообщения: MESSAGE, JOIN, LEAVE, TYPING, READ, ERROR
    private String type;

    // Данные сообщения
    private UUID messageId;
    private UUID requestId;
    private UUID senderId;
    private String senderName;
    private String message;
    private Instant timestamp;
    private Boolean isRead;

    // Дополнительные поля
    private String error;
    private Map<String, Object> metadata;

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public static WebSocketChatMessageDto fromResponseDto(ChatMessageResponseDto dto) {
        WebSocketChatMessageDto wsDto = new WebSocketChatMessageDto();
        wsDto.setType("MESSAGE");
        wsDto.setMessageId(dto.getId());
        wsDto.setRequestId(dto.getRequestId());
        wsDto.setSenderId(dto.getSenderId());
        wsDto.setSenderName(dto.getSenderName());
        wsDto.setMessage(dto.getMessage());
        wsDto.setTimestamp(dto.getSentAt());
        wsDto.setIsRead(dto.getIsRead());
        return wsDto;
    }
}