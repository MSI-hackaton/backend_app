package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.ProjectPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectPhotoRepository extends JpaRepository<ProjectPhoto, UUID> {
    List<ProjectPhoto> findByProjectId(UUID projectId);

    @Query("SELECT COUNT(pp) FROM ProjectPhoto pp WHERE pp.photo.id = :photoId")
    long countByPhotoId(@Param("photoId") UUID photoId);
}