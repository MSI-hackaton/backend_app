package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConstructionStageRepository extends AbstractRepository<ConstructionStage> {
    List<ConstructionStage> findByProjectId(UUID projectId);

    List<ConstructionStage> findByCustomerId(UUID customerId);

    List<ConstructionStage> findBySpecialistId(UUID specialistId);

    List<ConstructionStage> findByRequestId(UUID requestId);
}
