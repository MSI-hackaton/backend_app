package dev.msi_hackaton.backend_app.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ReportPhotoResponseDto {
    private UUID id;
    private UUID reportId;
    private String url;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}