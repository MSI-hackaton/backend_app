package dev.msi_hackaton.backend_app.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketChatMessageDto {
    private UUID id;
    private UUID requestId;
    private UUID senderId;
    private String senderName;
    private String message;
    private Boolean isRead;
    private Instant createdAt;
}