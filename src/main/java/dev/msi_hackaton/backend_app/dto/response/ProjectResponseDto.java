package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import dev.msi_hackaton.backend_app.dto.nested.ProjectPhotoNestedDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProjectResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Double area;
    private Integer floors;
    private Double price;
    private Integer constructionTime;
    private ProjectStatus status;
    private List<ProjectPhotoNestedDto> photos;
}
