package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentAgreementCreateDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private Boolean requiredSignatures;
    private String deadline; // ISO формат
}