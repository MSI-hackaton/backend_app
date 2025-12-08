package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dto.nested.PhotoNestedDto;
import lombok.Data;

import java.util.UUID;

@Data
public class ProjectPhotoResponseDto {
    private UUID id;
    private PhotoNestedDto photo;
    private Integer sortOrder;
    private String description;
}
