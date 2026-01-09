package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StageReportCreateDto {
    @NotBlank(message = "Description is required")
    private String description;
}