package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.DocumentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentNotificationRepository extends JpaRepository<DocumentNotification, UUID> {
    List<DocumentNotification> findByUserIdAndIsReadFalse(UUID userId);
    List<DocumentNotification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<DocumentNotification> findByDocumentId(UUID documentId);
    long countByUserIdAndIsReadFalse(UUID userId);
}