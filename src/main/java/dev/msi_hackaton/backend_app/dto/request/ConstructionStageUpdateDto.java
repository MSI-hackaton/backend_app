package dev.msi_hackaton.backend_app.dto.request;

import dev.msi_hackaton.backend_app.dao.entities.enums.StageStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class ConstructionStageUpdateDto {
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private StageStatus status;
}
