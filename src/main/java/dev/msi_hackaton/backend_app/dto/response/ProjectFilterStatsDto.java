package dev.msi_hackaton.backend_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterStatsDto {
    private Double minArea;
    private Double maxArea;
    private Double minPrice;
    private Double maxPrice;
    private Integer minFloors;
    private Integer maxFloors;
}