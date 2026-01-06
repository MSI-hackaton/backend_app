package dev.msi_hackaton.backend_app.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDto {
    private MessageType type;
    private String content;
    private UUID senderId;
    private UUID constructionId;
    private String timestamp;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        TYPING,
        READ_RECEIPT
    }
}