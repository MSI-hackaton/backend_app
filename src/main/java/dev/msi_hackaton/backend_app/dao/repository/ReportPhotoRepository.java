package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ReportPhoto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportPhotoRepository extends AbstractRepository<ReportPhoto> {
    List<ReportPhoto> findByReportId(UUID reportId);
}
