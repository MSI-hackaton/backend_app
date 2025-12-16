package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ConstructionRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "text", length = 5000, nullable = false)  // 5KB лимит
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "boolean DEFAULT false")
    private Boolean isRead;
}