package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentUploadDto {
    @NotBlank(message = "Document name is required")
    private String name;

    private String description;
}