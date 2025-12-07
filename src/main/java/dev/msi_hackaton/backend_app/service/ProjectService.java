package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dto.request.ProjectRequest;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponse;
import dev.msi_hackaton.backend_app.entity.Project;
import dev.msi_hackaton.backend_app.repository.ProjectRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(Pageable pageable,
                                                BigDecimal minArea,
                                                BigDecimal maxArea,
                                                Integer minFloors,
                                                Integer maxFloors,
                                                BigDecimal minPrice,
                                                BigDecimal maxPrice) {

        Specification<Project> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("isActive"), true));

            if (minArea != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea));
            }
            if (maxArea != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea));
            }
            if (minFloors != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("floorCount"), minFloors));
            }
            if (maxFloors != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("floorCount"), maxFloors));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return projectRepository.findAll(spec, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return convertToResponse(project);
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .area(request.getArea())
                .floorCount(request.getFloorCount())
                .bedroomCount(request.getBedroomCount())
                .bathroomCount(request.getBathroomCount())
                .price(request.getPrice())
                .constructionTimeMonths(request.getConstructionTimeMonths())
                .imageUrls(request.getImageUrls())
                .build();

        Project saved = projectRepository.save(project);
        return convertToResponse(saved);
    }

    private ProjectResponse convertToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setArea(project.getArea());
        response.setFloorCount(project.getFloorCount());
        response.setBedroomCount(project.getBedroomCount());
        response.setBathroomCount(project.getBathroomCount());
        response.setPrice(project.getPrice());
        response.setConstructionTimeMonths(project.getConstructionTimeMonths());
        response.setImageUrls(project.getImageUrls());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }
}