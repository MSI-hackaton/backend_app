package dev.msi_hackaton.backend_app.dto.request;

import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import lombok.Data;

@Data
public class ProjectCreateDto {
    private String title;
    private String description;
    private Double area;
    private Integer floors;
    private Double price;
    private Integer constructionTime;
    private ProjectStatus status;
}
