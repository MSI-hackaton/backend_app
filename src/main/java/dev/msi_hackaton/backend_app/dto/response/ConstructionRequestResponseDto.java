package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.RequestStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ConstructionRequestResponseDto {
    private UUID id;
    private UUID projectId;
    private String anonymousFullName;
    private String anonymousEmail;
    private String anonymousPhone;
    private RequestStatus status;
    private Instant createdAt;
}
