package dev.msi_hackaton.backend_app.dto.request;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ConstructionStageCreateDto {
    private UUID requestId;
    private UUID projectId;
    private UUID customerId;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
}
