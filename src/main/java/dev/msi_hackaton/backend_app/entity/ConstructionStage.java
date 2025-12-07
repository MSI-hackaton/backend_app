package dev.msi_hackaton.backend_app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "construction_stages")
public class ConstructionStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageStatus status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "completion_percentage")
    private Integer completionPercentage = 0;

    @ElementCollection
    @CollectionTable(name = "stage_images", joinColumns = @JoinColumn(name = "stage_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "video_url")
    private String videoStreamUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = StageStatus.PENDING;
        }
    }

    public enum StageStatus {
        PENDING,      // Ожидает начала
        IN_PROGRESS,  // В процессе
        COMPLETED,    // Завершен
        DELAYED       // Задержан
    }

    // Конструкторы
    public ConstructionStage() {}

    public ConstructionStage(Long id, Order order, String title, String description, Integer sequence,
                             Integer durationDays, StageStatus status, LocalDateTime startDate,
                             LocalDateTime endDate, Integer completionPercentage, List<String> imageUrls,
                             String videoStreamUrl, LocalDateTime createdAt) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.description = description;
        this.sequence = sequence;
        this.durationDays = durationDays;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completionPercentage = completionPercentage;
        this.imageUrls = imageUrls;
        this.videoStreamUrl = videoStreamUrl;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public StageStatus getStatus() { return status; }
    public void setStatus(StageStatus status) { this.status = status; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(Integer completionPercentage) { this.completionPercentage = completionPercentage; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getVideoStreamUrl() { return videoStreamUrl; }
    public void setVideoStreamUrl(String videoStreamUrl) { this.videoStreamUrl = videoStreamUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}