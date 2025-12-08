package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessage extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ConstructionRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "message", length = 5000, nullable = false)
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean DEFAULT false")
    private Boolean isRead;

    public ChatMessage() {
    }

    public ConstructionRequest getRequest() {
        return request;
    }

    public void setRequest(ConstructionRequest request) {
        this.request = request;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}