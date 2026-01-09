package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class DocumentResponseDto {
    private UUID id;
    private UUID constructionId;
    private String name;
    private String description;
    private String fileUrl;
    private String originalFilename;
    private Long fileSize;
    private String mimeType;
    private DocumentStatus status;
    private Instant createdAt;
    private Instant reviewedAt;
    private UUID reviewedById;
    private String reviewedByName;
}