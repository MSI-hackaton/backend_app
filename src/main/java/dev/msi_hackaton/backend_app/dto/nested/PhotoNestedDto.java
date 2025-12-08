package dev.msi_hackaton.backend_app.dto.nested;

import lombok.Data;

import java.util.UUID;

@Data
public class PhotoNestedDto {
    private UUID id;
    private String url;
}
