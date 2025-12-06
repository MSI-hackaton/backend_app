package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, UUID> {
    List<ReportPhoto> findByReportId(UUID reportId);
}
