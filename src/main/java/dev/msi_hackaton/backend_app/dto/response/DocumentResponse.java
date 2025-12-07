package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.entity.Document;
import java.time.LocalDateTime;

public class DocumentResponse {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private String type;
    private String status;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;

    public DocumentResponse() {}

    public DocumentResponse(Long id, String title, String description, String fileUrl, String type,
                            String status, LocalDateTime signedAt, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.type = type;
        this.status = status;
        this.signedAt = signedAt;
        this.createdAt = createdAt;
    }

    public static DocumentResponse fromEntity(Document document) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setTitle(document.getTitle());
        response.setDescription(document.getDescription());
        response.setFileUrl(document.getFileUrl());
        response.setType(document.getType().name());
        response.setStatus(document.getStatus().name());
        response.setSignedAt(document.getSignedAt());
        response.setCreatedAt(document.getCreatedAt());
        return response;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}