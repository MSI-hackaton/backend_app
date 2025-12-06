package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список проектов", description = "Возвращает все доступные проекты")
    public List<ProjectResponseDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить проект по ID", description = "Возвращает проект по его идентификатору")
    public ProjectResponseDto getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать проект", description = "Создаёт новый строительный проект")
    public ProjectResponseDto createProject(@RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить проект", description = "Удаляет проект по идентификатору")
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
}
