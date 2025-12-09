package dev.msi_hackaton.backend_app.dto.websocket;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class WebSocketMessageDto {
    private String type; // CONNECT, MESSAGE, JOIN, LEAVE, ERROR
    private UUID messageId;
    private UUID requestId;
    private UUID senderId;
    private String senderName;
    private String message;
    private Instant timestamp;
    private Boolean isRead;
    private String error;
}