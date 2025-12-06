package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConstructionRequestRepository extends AbstractRepository<ConstructionRequest> {
    List<ConstructionRequest> findByUserId(UUID userId);
}
