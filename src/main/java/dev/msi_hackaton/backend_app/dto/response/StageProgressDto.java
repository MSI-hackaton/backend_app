package dev.msi_hackaton.backend_app.dto.response;

import lombok.Data;

@Data
public class StageProgressDto {
    private int totalStages;
    private long completedStages;
    private long inProgressStages;
    private long plannedStages;
    private int overallProgress;
    private String currentStageName;
}