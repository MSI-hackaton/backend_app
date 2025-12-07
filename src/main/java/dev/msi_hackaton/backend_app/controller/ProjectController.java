package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ProjectRequest;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponse;
import dev.msi_hackaton.backend_app.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Проекты", description = "API для работы с проектами домов")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Получить список проектов с фильтрацией")
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) BigDecimal minArea,
            @RequestParam(required = false) BigDecimal maxArea,
            @RequestParam(required = false) Integer minFloors,
            @RequestParam(required = false) Integer maxFloors,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        Page<ProjectResponse> projects = projectService.getAllProjects(
                pageable, minArea, maxArea, minFloors, maxFloors, minPrice, maxPrice
        );
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить детали проекта")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    @Operation(summary = "Создать новый проект")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse project = projectService.createProject(request);
        return ResponseEntity.ok(project);
    }
}