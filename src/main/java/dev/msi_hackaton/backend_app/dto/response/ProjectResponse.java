package dev.msi_hackaton.backend_app.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal area;
    private Integer floorCount;
    private Integer bedroomCount;
    private Integer bathroomCount;
    private BigDecimal price;
    private Integer constructionTimeMonths;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    public ProjectResponse() {}

    public ProjectResponse(Long id, String title, String description, BigDecimal area, Integer floorCount,
                           Integer bedroomCount, Integer bathroomCount, BigDecimal price,
                           Integer constructionTimeMonths, List<String> imageUrls, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.area = area;
        this.floorCount = floorCount;
        this.bedroomCount = bedroomCount;
        this.bathroomCount = bathroomCount;
        this.price = price;
        this.constructionTimeMonths = constructionTimeMonths;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public Integer getFloorCount() { return floorCount; }
    public void setFloorCount(Integer floorCount) { this.floorCount = floorCount; }

    public Integer getBedroomCount() { return bedroomCount; }
    public void setBedroomCount(Integer bedroomCount) { this.bedroomCount = bedroomCount; }

    public Integer getBathroomCount() { return bathroomCount; }
    public void setBathroomCount(Integer bathroomCount) { this.bathroomCount = bathroomCount; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getConstructionTimeMonths() { return constructionTimeMonths; }
    public void setConstructionTimeMonths(Integer constructionTimeMonths) { this.constructionTimeMonths = constructionTimeMonths; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}