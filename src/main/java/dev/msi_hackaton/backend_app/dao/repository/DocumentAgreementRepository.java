package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.DocumentAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentAgreementRepository extends JpaRepository<DocumentAgreement, UUID> {
    List<DocumentAgreement> findByConstructionRequestIdOrderByCreatedAtDesc(UUID requestId);
    List<DocumentAgreement> findByConstructionRequestIdAndStatus(UUID requestId, String status);
}