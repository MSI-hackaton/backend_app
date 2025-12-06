package dev.msi_hackaton.backend_app.dao.repository;

import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends AbstractRepository<Project> {
    List<Project> findByStatus(ProjectStatus status);
}
