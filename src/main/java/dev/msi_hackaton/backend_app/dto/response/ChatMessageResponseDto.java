package dev.msi_hackaton.backend_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private UUID id;
    private UUID requestId;
    private UUID senderId;
    private String senderName;
    private String message;
    private Boolean isRead;
    private Instant createdAt;
}