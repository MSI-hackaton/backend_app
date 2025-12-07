package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private ProjectResponse project;
    private String status;
    private BigDecimal totalPrice;
    private String constructionAddress;
    private LocalDateTime startDate;
    private LocalDateTime estimatedEndDate;
    private LocalDateTime actualEndDate;
    private LocalDateTime createdAt;

    public OrderResponse() {}

    public OrderResponse(Long id, ProjectResponse project, String status, BigDecimal totalPrice,
                         String constructionAddress, LocalDateTime startDate, LocalDateTime estimatedEndDate,
                         LocalDateTime actualEndDate, LocalDateTime createdAt) {
        this.id = id;
        this.project = project;
        this.status = status;
        this.totalPrice = totalPrice;
        this.constructionAddress = constructionAddress;
        this.startDate = startDate;
        this.estimatedEndDate = estimatedEndDate;
        this.actualEndDate = actualEndDate;
        this.createdAt = createdAt;
    }

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus().name());
        response.setTotalPrice(order.getTotalPrice());
        response.setConstructionAddress(order.getConstructionAddress());
        response.setStartDate(order.getStartDate());
        response.setEstimatedEndDate(order.getEstimatedEndDate());
        response.setActualEndDate(order.getActualEndDate());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProjectResponse getProject() { return project; }
    public void setProject(ProjectResponse project) { this.project = project; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}