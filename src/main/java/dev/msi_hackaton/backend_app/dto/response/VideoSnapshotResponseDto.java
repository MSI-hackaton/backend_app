package dev.msi_hackaton.backend_app.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class VideoSnapshotResponseDto {
    private UUID id;
    private UUID streamId;
    private String imageUrl;
    private Instant timestamp;
    private String description;
}