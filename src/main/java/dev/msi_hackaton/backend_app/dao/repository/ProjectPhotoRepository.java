package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ProjectPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectPhotoRepository extends JpaRepository<ProjectPhoto, UUID> {
    List<ProjectPhoto> findByProjectId(UUID projectId);
}
