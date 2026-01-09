package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.Document;
import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByConstructionIdOrderByCreatedAt(UUID constructionId);
    List<Document> findByConstructionIdAndStatus(UUID constructionId, DocumentStatus status);

    @Query("SELECT d FROM Document d WHERE d.construction.id = :constructionId AND d.status IN :statuses")
    List<Document> findByConstructionIdAndStatusIn(
            @Param("constructionId") UUID constructionId,
            @Param("statuses") List<DocumentStatus> statuses
    );

    long countByConstructionIdAndStatus(UUID constructionId, DocumentStatus status);
}