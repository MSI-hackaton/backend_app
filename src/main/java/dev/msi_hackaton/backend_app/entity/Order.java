package dev.msi_hackaton.backend_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "address")
    private String constructionAddress;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "estimated_end_date")
    private LocalDateTime estimatedEndDate;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ConstructionStage> constructionStages = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private Long userId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    public enum OrderStatus {
        PENDING,           // Заявка подана
        DOCUMENTS_PENDING, // Ожидание подписания документов
        PREPARATION,       // Подготовка к строительству
        IN_PROGRESS,       // Строительство в процессе
        COMPLETION,        // Завершение работ
        FINAL_DOCUMENTS,   // Подписание финальных документов
        WARRANTY,          // Гарантийное обслуживание
        COMPLETED,         // Проект завершен
        CANCELLED          // Проект отменен
    }

    // Конструкторы
    public Order() {}

    public Order(User user, Project project, String comment, String address) {
        this.user = user;
        this.project = project;
        this.comment = comment;
        this.constructionAddress = address;
        this.status = OrderStatus.PENDING;
        this.totalPrice = project.getPrice();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getConstructionAddress() { return constructionAddress; }
    public void setConstructionAddress(String constructionAddress) { this.constructionAddress = constructionAddress; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEstimatedEndDate() { return estimatedEndDate; }
    public void setEstimatedEndDate(LocalDateTime estimatedEndDate) { this.estimatedEndDate = estimatedEndDate; }

    public LocalDateTime getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDateTime actualEndDate) { this.actualEndDate = actualEndDate; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public List<ConstructionStage> getConstructionStages() { return constructionStages; }
    public void setConstructionStages(List<ConstructionStage> constructionStages) { this.constructionStages = constructionStages; }

    public List<ChatMessage> getChatMessages() { return chatMessages; }
    public void setChatMessages(List<ChatMessage> chatMessages) { this.chatMessages = chatMessages; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() {
        return user != null ? user.getId() : userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Вспомогательные методы
    public boolean isInProgress() {
        return status == OrderStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == OrderStatus.COMPLETED || status == OrderStatus.WARRANTY;
    }

    public boolean canStartConstruction() {
        return status == OrderStatus.PREPARATION;
    }

    public boolean canSignDocuments() {
        return status == OrderStatus.DOCUMENTS_PENDING || status == OrderStatus.FINAL_DOCUMENTS;
    }

    public double getProgressPercentage() {
        if (constructionStages == null || constructionStages.isEmpty()) {
            return 0.0;
        }

        double totalPercentage = constructionStages.stream()
                .mapToInt(stage -> stage.getCompletionPercentage() != null ? stage.getCompletionPercentage() : 0)
                .average()
                .orElse(0.0);

        return Math.round(totalPercentage * 10) / 10.0;
    }

    public ConstructionStage getCurrentStage() {
        if (constructionStages == null || constructionStages.isEmpty()) {
            return null;
        }

        return constructionStages.stream()
                .filter(stage -> stage.getStatus() == ConstructionStage.StageStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);
    }
}