package dev.msi_hackaton.backend_app.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class VideoStreamResponseDto {
    private UUID id;
    private UUID requestId;
    private String streamUrl;
    private String cameraName;
    private String cameraLocation;
    private String thumbnailUrl;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}