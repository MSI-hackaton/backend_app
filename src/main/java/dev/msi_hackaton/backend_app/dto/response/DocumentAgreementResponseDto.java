package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentAgreementStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class DocumentAgreementResponseDto {
    private UUID id;
    private UUID requestId;
    private String title;
    private String description;
    private DocumentAgreementStatus status;
    private Boolean requiredSignatures;
    private Instant deadline;
    private Instant signedAt;
    private UUID signedById;
    private String signedByName;
    private Instant createdAt;
}