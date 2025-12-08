package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ProjectPhoto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectPhotoRepository extends AbstractRepository<ProjectPhoto> {
    List<ProjectPhoto> findByProjectId(UUID projectId);
}
