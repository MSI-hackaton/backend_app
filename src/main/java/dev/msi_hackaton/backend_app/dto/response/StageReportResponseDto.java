package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.ReportStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class StageReportResponseDto {
    private UUID id;
    private UUID stageId;
    private String stageName;
    private String description;
    private ReportStatus status;
    private List<ReportPhotoResponseDto> photos;
    private Instant createdAt;
    private Instant updatedAt;
}