package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ChatMessage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends AbstractRepository<ChatMessage> {
    List<ChatMessage> findByRequestIdOrderByCreatedAt(UUID requestId);
    List<ChatMessage> findByRequestIdAndIsReadFalse(UUID requestId);
    Long countByRequestIdAndIsReadFalse(UUID requestId);
}
