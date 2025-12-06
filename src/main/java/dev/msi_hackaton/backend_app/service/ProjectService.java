package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.repository.ProjectPhotoRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPhotoRepository projectPhotoRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectResponseDto createProject(ProjectCreateDto projectCreateDto) {
        Project project = projectMapper.toEntity(projectCreateDto);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }
}
