package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.Photo;
import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.ProjectPhoto;
import dev.msi_hackaton.backend_app.dao.repository.PhotoRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectPhotoRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.request.ProjectPhotoCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectPhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
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
    private final PhotoRepository photoRepository;
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

    @Transactional
    public ProjectPhotoResponseDto addPhotoToProject(UUID projectId, ProjectPhotoCreateDto projectPhotoCreateDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Photo photo = photoRepository.findById(projectPhotoCreateDto.getPhotoId())
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        ProjectPhoto projectPhoto = projectMapper.toEntity(projectPhotoCreateDto);
        projectPhoto.setProject(project);
        projectPhoto.setPhoto(photo);

        ProjectPhoto savedProjectPhoto = projectPhotoRepository.save(projectPhoto);
        return projectMapper.toProjectPhotoDto(savedProjectPhoto);
    }

    @Transactional(readOnly = true)
    public List<ProjectPhotoResponseDto> getProjectPhotos(UUID projectId) {
        return projectPhotoRepository.findByProjectId(projectId).stream()
                .map(projectMapper::toProjectPhotoDto)
                .toList();
    }

    @Transactional
    public void deleteProjectPhoto(UUID projectPhotoId) {
        ProjectPhoto projectPhoto = projectPhotoRepository.findById(projectPhotoId)
                .orElseThrow(() -> new EntityNotFoundException("Project photo not found with id: " + projectPhotoId));
        projectPhotoRepository.delete(projectPhoto);
    }
}
