package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VideoStreamCreateDto {
    @NotBlank(message = "Stream URL is required")
    private String streamUrl;

    @NotBlank(message = "Camera name is required")
    private String cameraName;

    private String cameraLocation;
    private String thumbnailUrl;
}