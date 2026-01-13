package dev.msi_hackaton.backend_app.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentNotificationDto {
    private UUID id;
    private UUID documentId;
    private UUID userId;
    private String notificationType;
    private String message;
    private Boolean isRead;
    private Instant createdAt;
    private Instant readAt;

    // Дополнительные данные для фронтенда
    private String documentName;
    private String constructionName;
    private DocumentStatusDto documentStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentStatusDto {
        private String status;
        private String displayName;
    }
}