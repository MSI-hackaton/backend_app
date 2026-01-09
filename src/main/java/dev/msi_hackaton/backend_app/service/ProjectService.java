package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.Photo;
import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.ProjectPhoto;
import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import dev.msi_hackaton.backend_app.dao.repository.PhotoRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectPhotoRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.request.ProjectPhotoCreateDto;
import dev.msi_hackaton.backend_app.dto.response.PhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectFilterStatsDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectPhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectPhotoRepository projectPhotoRepository;
    private final PhotoRepository photoRepository;
    private final ProjectMapper projectMapper;
    private final PhotoService photoService;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getAllProjects(
            Double minArea, Double maxArea,
            Integer minFloors, Integer maxFloors,
            Double minPrice, Double maxPrice,
            String status, String search,
            Pageable pageable) {

        Specification<Project> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по площади
            if (minArea != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea));
            }
            if (maxArea != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea));
            }

            // Фильтр по этажам
            if (minFloors != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("floors"), minFloors));
            }
            if (maxFloors != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("floors"), maxFloors));
            }

            // Фильтр по цене
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Фильтр по статусу
            if (status != null && !status.isEmpty()) {
                try {
                    ProjectStatus projectStatus = ProjectStatus.valueOf(status.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), projectStatus));
                } catch (IllegalArgumentException e) {
                    // Игнорируем неверный статус
                }
            }

            // Поиск по названию и описанию
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), searchPattern
                );
                Predicate descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchPattern
                );
                predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
            }

            // Только активные проекты (не удаленные)
            predicates.add(criteriaBuilder.isNotNull(root.get("id")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return projectRepository.findAll(spec, pageable)
                .map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getAvailableProjects(
            Double minArea, Double maxArea,
            Double minPrice, Double maxPrice,
            Pageable pageable) {

        Specification<Project> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Только доступные проекты
            predicates.add(criteriaBuilder.equal(root.get("status"), ProjectStatus.AVAILABLE));

            // Фильтр по площади
            if (minArea != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea));
            }
            if (maxArea != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea));
            }

            // Фильтр по цене
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return projectRepository.findAll(spec, pageable)
                .map(projectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
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
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        // Удаляем все связанные фотографии
        List<ProjectPhoto> projectPhotos = projectPhotoRepository.findByProjectId(id);
        for (ProjectPhoto projectPhoto : projectPhotos) {
            deleteProjectPhotoInternal(projectPhoto);
        }

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectPhotoResponseDto addPhotoToProject(UUID projectId, ProjectPhotoCreateDto projectPhotoCreateDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        Photo photo = photoRepository.findById(projectPhotoCreateDto.getPhotoId())
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + projectPhotoCreateDto.getPhotoId()));

        ProjectPhoto projectPhoto = projectMapper.toEntity(projectPhotoCreateDto);
        projectPhoto.setProject(project);
        projectPhoto.setPhoto(photo);

        // Если sortOrder не указан, устанавливаем максимальный + 1
        if (projectPhoto.getSortOrder() == null) {
            Integer maxOrder = projectPhotoRepository.findByProjectId(projectId).stream()
                    .map(ProjectPhoto::getSortOrder)
                    .filter(order -> order != null)
                    .max(Comparator.naturalOrder())
                    .orElse(0);
            projectPhoto.setSortOrder(maxOrder + 1);
        }

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
        deleteProjectPhotoInternal(projectPhoto);
    }

    @Transactional
    public PhotoResponseDto uploadAndAttachPhotoToProject(UUID projectId, MultipartFile file, String description) throws IOException {
        // Загружаем фото через PhotoService
        PhotoResponseDto photoDto = photoService.uploadPhoto(file);

        // Создаем связь с проектом
        ProjectPhotoCreateDto projectPhotoDto = new ProjectPhotoCreateDto();
        projectPhotoDto.setPhotoId(photoDto.getId());
        projectPhotoDto.setDescription(description);

        // Добавляем сортировку по умолчанию
        Integer maxOrder = projectPhotoRepository.findByProjectId(projectId).stream()
                .map(ProjectPhoto::getSortOrder)
                .filter(order -> order != null)
                .max(Comparator.naturalOrder())
                .orElse(0);
        projectPhotoDto.setSortOrder(maxOrder + 1);

        addPhotoToProject(projectId, projectPhotoDto);

        return photoDto;
    }

    @Transactional(readOnly = true)
    public List<ProjectPhotoResponseDto> getProjectPhotosWithDetails(UUID projectId) {
        return getProjectPhotos(projectId).stream()
                .sorted(Comparator.comparing(ProjectPhotoResponseDto::getSortOrder))
                .toList();
    }

    @Transactional
    public void reorderProjectPhotos(UUID projectId, List<UUID> photoIdsInOrder) {
        List<ProjectPhoto> projectPhotos = projectPhotoRepository.findByProjectId(projectId);

        // Проверяем, что все фото принадлежат проекту
        List<UUID> existingPhotoIds = projectPhotos.stream()
                .map(pp -> pp.getPhoto().getId())
                .toList();

        if (!existingPhotoIds.containsAll(photoIdsInOrder)) {
            throw new EntityNotFoundException("Some photos don't belong to the project");
        }

        // Обновляем порядок
        for (int i = 0; i < photoIdsInOrder.size(); i++) {
            UUID photoId = photoIdsInOrder.get(i);
            ProjectPhoto projectPhoto = projectPhotos.stream()
                    .filter(pp -> pp.getPhoto().getId().equals(photoId))
                    .findFirst()
                    .orElseThrow();
            projectPhoto.setSortOrder(i + 1);
        }

        projectPhotoRepository.saveAll(projectPhotos);
    }

    @Transactional(readOnly = true)
    public ProjectFilterStatsDto getFilterStats() {
        ProjectFilterStatsDto stats = new ProjectFilterStatsDto();

        // Получаем минимальные и максимальные значения
        stats.setMinArea(projectRepository.getMinArea());
        stats.setMaxArea(projectRepository.getMaxArea());
        stats.setMinPrice(projectRepository.getMinPrice());
        stats.setMaxPrice(projectRepository.getMaxPrice());
        stats.setMinFloors(projectRepository.getMinFloors());
        stats.setMaxFloors(projectRepository.getMaxFloors());

        return stats;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getSimilarProjects(UUID projectId, int limit) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        if (project.getArea() == null || project.getPrice() == null || project.getFloors() == null) {
            return List.of();
        }

        // Находим похожие проекты по характеристикам
        List<Project> similarProjects = projectRepository.findSimilarProjects(
                project.getArea(),
                project.getPrice(),
                project.getFloors(),
                projectId
        );

        // Ограничиваем количество результатов
        return similarProjects.stream()
                .limit(limit)
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status).stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional
    public ProjectResponseDto updateProjectStatus(UUID projectId, ProjectStatus status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        project.setStatus(status);
        Project updated = projectRepository.save(project);
        return projectMapper.toDto(updated);
    }

    @Transactional
    public ProjectResponseDto updateProjectInfo(UUID projectId, String title, String description,
                                                Double area, Integer floors, Double price,
                                                Integer constructionTime) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        if (title != null && !title.trim().isEmpty()) {
            project.setTitle(title);
        }

        if (description != null) {
            project.setDescription(description);
        }

        if (area != null) {
            project.setArea(area);
        }

        if (floors != null) {
            project.setFloors(floors);
        }

        if (price != null) {
            project.setPrice(price);
        }

        if (constructionTime != null) {
            project.setConstructionTime(constructionTime);
        }

        Project updated = projectRepository.save(project);
        return projectMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> searchProjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        String searchPattern = "%" + keyword.toLowerCase() + "%";
        List<Project> projects = projectRepository.searchByTitleOrDescription(searchPattern);

        return projects.stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countAvailableProjects() {
        return projectRepository.countByStatus(ProjectStatus.AVAILABLE);
    }

    private void deleteProjectPhotoInternal(ProjectPhoto projectPhoto) {
        // Удаляем связь проекта с фото
        projectPhotoRepository.delete(projectPhoto);

        // Опционально: удаляем само фото, если оно больше нигде не используется
        UUID photoId = projectPhoto.getPhoto().getId();
        long usageCount = projectPhotoRepository.countByPhotoId(photoId);

        if (usageCount == 0) {
            photoService.deletePhoto(photoId);
        }
    }
}