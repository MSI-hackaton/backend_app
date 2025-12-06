package dev.msi_hackaton.backend_app.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class ProjectPhotoCreateDto {
    private UUID photoId;
    private Integer sortOrder;
    private String description;
}
