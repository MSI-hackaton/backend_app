package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ProjectCreateDto;
import dev.msi_hackaton.backend_app.dto.request.ProjectPhotoCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectFilterStatsDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectPhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ProjectResponseDto;
import dev.msi_hackaton.backend_app.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "API для работы с проектами домов")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    @Operation(
            summary = "Получить список проектов с фильтрами",
            description = """
            Возвращает список всех доступных проектов с пагинацией и фильтрацией.
            
            Поддерживаемые фильтры:
            - minArea, maxArea - фильтрация по площади (м²)
            - minFloors, maxFloors - фильтрация по количеству этажей
            - minPrice, maxPrice - фильтрация по стоимости (руб.)
            - status - фильтрация по статусу проекта
            
            Пагинация:
            - page - номер страницы (начинается с 0)
            - size - количество элементов на странице (по умолчанию 10)
            - sort - сортировка (например: area,asc или price,desc)
            """
    )
    public Page<ProjectResponseDto> getAllProjects(
            @Parameter(description = "Минимальная площадь (м²)")
            @RequestParam(required = false) Double minArea,

            @Parameter(description = "Максимальная площадь (м²)")
            @RequestParam(required = false) Double maxArea,

            @Parameter(description = "Минимальное количество этажей")
            @RequestParam(required = false) Integer minFloors,

            @Parameter(description = "Максимальное количество этажей")
            @RequestParam(required = false) Integer maxFloors,

            @Parameter(description = "Минимальная стоимость (руб.)")
            @RequestParam(required = false) Double minPrice,

            @Parameter(description = "Максимальная стоимость (руб.)")
            @RequestParam(required = false) Double maxPrice,

            @Parameter(description = "Статус проекта: AVAILABLE, UNDER_CONSTRUCTION, COMPLETED")
            @RequestParam(required = false) String status,

            @Parameter(description = "Поиск по названию или описанию")
            @RequestParam(required = false) String search,

            @PageableDefault(size = 10)
            @Parameter(description = "Параметры пагинации и сортировки")
            Pageable pageable) {

        return projectService.getAllProjects(
                minArea, maxArea,
                minFloors, maxFloors,
                minPrice, maxPrice,
                status, search,
                pageable
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить проект по ID",
            description = "Возвращает детальную информацию о проекте по его идентификатору"
    )
    public ProjectResponseDto getProjectById(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать проект",
            description = "Создаёт новый строительный проект (только для администраторов)"
    )
    public ProjectResponseDto createProject(
            @Parameter(description = "Данные нового проекта", required = true)
            @RequestBody ProjectCreateDto projectCreateDto) {
        return projectService.createProject(projectCreateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить проект",
            description = "Удаляет проект по идентификатору (только для администраторов)"
    )
    public void deleteProject(
            @Parameter(description = "ID проекта для удаления", required = true)
            @PathVariable UUID id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{projectId}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить фотографию к проекту",
            description = "Создаёт новую запись фотографии и привязывает её к указанному проекту"
    )
    public ProjectPhotoResponseDto addPhotoToProject(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable UUID projectId,

            @Parameter(description = "Данные фотографии проекта", required = true)
            @RequestBody ProjectPhotoCreateDto projectPhotoCreateDto) {
        return projectService.addPhotoToProject(projectId, projectPhotoCreateDto);
    }

    @GetMapping("/{projectId}/photos")
    @Operation(
            summary = "Получить фотографии проекта",
            description = "Возвращает список всех фотографий, привязанных к проекту"
    )
    public List<ProjectPhotoResponseDto> getProjectPhotos(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable UUID projectId) {
        return projectService.getProjectPhotos(projectId);
    }

    @DeleteMapping("/photos/{projectPhotoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить фотографию проекта",
            description = "Удаляет фотографию проекта по её идентификатору"
    )
    public void deleteProjectPhoto(
            @Parameter(description = "ID фотографии проекта", required = true)
            @PathVariable UUID projectPhotoId) {
        projectService.deleteProjectPhoto(projectPhotoId);
    }

    @GetMapping("/search/available")
    @Operation(
            summary = "Поиск доступных проектов",
            description = "Возвращает только доступные проекты с поддержкой фильтрации"
    )
    public Page<ProjectResponseDto> getAvailableProjects(
            @Parameter(description = "Минимальная площадь (м²)")
            @RequestParam(required = false) Double minArea,

            @Parameter(description = "Максимальная площадь (м²)")
            @RequestParam(required = false) Double maxArea,

            @Parameter(description = "Минимальная стоимость (руб.)")
            @RequestParam(required = false) Double minPrice,

            @Parameter(description = "Максимальная стоимость (руб.)")
            @RequestParam(required = false) Double maxPrice,

            @PageableDefault(size = 12)
            @Parameter(description = "Параметры пагинации")
            Pageable pageable) {

        return projectService.getAvailableProjects(minArea, maxArea, minPrice, maxPrice, pageable);
    }

    @GetMapping("/stats/filters")
    @Operation(
            summary = "Получить статистику для фильтров",
            description = "Возвращает минимальные и максимальные значения для фильтров проектов"
    )
    public ProjectFilterStatsDto getFilterStats() {
        return projectService.getFilterStats();
    }

    @GetMapping("/{projectId}/similar")
    @Operation(
            summary = "Получить похожие проекты",
            description = "Возвращает проекты, похожие на указанный по характеристикам"
    )
    public List<ProjectResponseDto> getSimilarProjects(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable UUID projectId,

            @Parameter(description = "Максимальное количество похожих проектов")
            @RequestParam(defaultValue = "4") int limit) {
        return projectService.getSimilarProjects(projectId, limit);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Получить проекты по статусу",
            description = "Возвращает проекты с указанным статусом"
    )
    public List<ProjectResponseDto> getProjectsByStatus(
            @Parameter(description = "Статус проекта", required = true)
            @PathVariable String status) {
        return projectService.getProjectsByStatus(
                dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus.valueOf(status.toUpperCase())
        );
    }

    @PatchMapping("/{projectId}/status")
    @Operation(
            summary = "Обновить статус проекта",
            description = "Изменяет статус проекта (только для администраторов)"
    )
    public ProjectResponseDto updateProjectStatus(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable UUID projectId,

            @Parameter(description = "Новый статус", required = true)
            @RequestParam String status) {
        return projectService.updateProjectStatus(
                projectId,
                dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus.valueOf(status.toUpperCase())
        );
    }

    @PutMapping("/{projectId}")
    @Operation(
            summary = "Обновить информацию о проекте",
            description = "Обновляет информацию о проекте (только для администраторов)"
    )
    public ProjectResponseDto updateProjectInfo(
            @Parameter(description = "ID проекта", required = true)
            @PathVariable UUID projectId,

            @Parameter(description = "Название проекта")
            @RequestParam(required = false) String title,

            @Parameter(description = "Описание проекта")
            @RequestParam(required = false) String description,

            @Parameter(description = "Площадь (м²)")
            @RequestParam(required = false) Double area,

            @Parameter(description = "Количество этажей")
            @RequestParam(required = false) Integer floors,

            @Parameter(description = "Стоимость (руб.)")
            @RequestParam(required = false) Double price,

            @Parameter(description = "Срок строительства (дней)")
            @RequestParam(required = false) Integer constructionTime) {

        return projectService.updateProjectInfo(projectId, title, description, area, floors, price, constructionTime);
    }

    @GetMapping("/search/keyword")
    @Operation(
            summary = "Поиск проектов по ключевому слову",
            description = "Возвращает проекты, соответствующие поисковому запросу"
    )
    public List<ProjectResponseDto> searchProjects(
            @Parameter(description = "Ключевое слово для поиска", required = true)
            @RequestParam String keyword) {
        return projectService.searchProjects(keyword);
    }

    @GetMapping("/count/available")
    @Operation(
            summary = "Получить количество доступных проектов",
            description = "Возвращает количество проектов со статусом AVAILABLE"
    )
    public long countAvailableProjects() {
        return projectService.countAvailableProjects();
    }
}