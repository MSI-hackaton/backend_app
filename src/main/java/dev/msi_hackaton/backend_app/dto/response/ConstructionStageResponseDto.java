package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.StageStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ConstructionStageResponseDto {
    private UUID id;
    private UUID requestId;
    private UUID projectId;
    private UUID customerId;
    private UUID specialistId;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private StageStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
