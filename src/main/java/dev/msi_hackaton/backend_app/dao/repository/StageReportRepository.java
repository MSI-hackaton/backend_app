package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.StageReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StageReportRepository extends AbstractRepository<StageReport> {
    List<StageReport> findByStageId(UUID stageId);
}
