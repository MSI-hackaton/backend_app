package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class ProjectRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    private BigDecimal area;

    @NotNull(message = "Floor count is required")
    @Positive(message = "Floor count must be positive")
    private Integer floorCount;

    private Integer bedroomCount;
    private Integer bathroomCount;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private Integer constructionTimeMonths;
    private List<String> imageUrls;

    public ProjectRequest() {}

    public ProjectRequest(String title, String description, BigDecimal area, Integer floorCount,
                          Integer bedroomCount, Integer bathroomCount, BigDecimal price,
                          Integer constructionTimeMonths, List<String> imageUrls) {
        this.title = title;
        this.description = description;
        this.area = area;
        this.floorCount = floorCount;
        this.bedroomCount = bedroomCount;
        this.bathroomCount = bathroomCount;
        this.price = price;
        this.constructionTimeMonths = constructionTimeMonths;
        this.imageUrls = imageUrls;
    }

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
}