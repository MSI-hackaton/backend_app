package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id", nullable = false)
    private ConstructionStage construction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")  // 5KB лимит
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean DEFAULT false")
    private Boolean isRead;
}