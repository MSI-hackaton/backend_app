package dev.msi_hackaton.backend_app.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class PhotoResponseDto {
    private UUID id;
    private String url;
    private String description;
}
