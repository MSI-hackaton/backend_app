package dev.msi_hackaton.backend_app.dto.nested;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectPhotoNestedDto {
    private UUID id;
    private String url;
    private String description;
    private Integer sortOrder;
}
