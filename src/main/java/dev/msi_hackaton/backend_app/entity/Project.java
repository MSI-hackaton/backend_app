package dev.msi_hackaton.backend_app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal area;

    @Column(name = "floor_count")
    private Integer floorCount;

    @Column(name = "bedroom_count")
    private Integer bedroomCount;

    @Column(name = "bathroom_count")
    private Integer bathroomCount;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "construction_time_months")
    private Integer constructionTimeMonths;

    @ElementCollection
    @CollectionTable(name = "project_images", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Конструкторы
    public Project() {}

    public Project(String title, String description, BigDecimal area, Integer floorCount,
                   Integer bedroomCount, Integer bathroomCount, BigDecimal price,
                   Integer constructionTimeMonths) {
        this.title = title;
        this.description = description;
        this.area = area;
        this.floorCount = floorCount;
        this.bedroomCount = bedroomCount;
        this.bathroomCount = bathroomCount;
        this.price = price;
        this.constructionTimeMonths = constructionTimeMonths;
    }

    // Builder pattern для удобства
    public static class Builder {
        private String title;
        private String description;
        private BigDecimal area;
        private Integer floorCount;
        private Integer bedroomCount;
        private Integer bathroomCount;
        private BigDecimal price;
        private Integer constructionTimeMonths;
        private List<String> imageUrls = new ArrayList<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder area(BigDecimal area) {
            this.area = area;
            return this;
        }

        public Builder floorCount(Integer floorCount) {
            this.floorCount = floorCount;
            return this;
        }

        public Builder bedroomCount(Integer bedroomCount) {
            this.bedroomCount = bedroomCount;
            return this;
        }

        public Builder bathroomCount(Integer bathroomCount) {
            this.bathroomCount = bathroomCount;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder constructionTimeMonths(Integer constructionTimeMonths) {
            this.constructionTimeMonths = constructionTimeMonths;
            return this;
        }

        public Builder imageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.title = this.title;
            project.description = this.description;
            project.area = this.area;
            project.floorCount = this.floorCount;
            project.bedroomCount = this.bedroomCount;
            project.bathroomCount = this.bathroomCount;
            project.price = this.price;
            project.constructionTimeMonths = this.constructionTimeMonths;
            if (this.imageUrls != null) {
                project.imageUrls = this.imageUrls;
            }
            return project;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Геттеры и сеттеры
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

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}