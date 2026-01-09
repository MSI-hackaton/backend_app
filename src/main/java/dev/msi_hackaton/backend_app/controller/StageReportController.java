package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.StageReportCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ReportPhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.StageReportResponseDto;
import dev.msi_hackaton.backend_app.service.ReportPhotoService;
import dev.msi_hackaton.backend_app.service.StageReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stage-reports")
@RequiredArgsConstructor
public class StageReportController {

    private final StageReportService stageReportService;
    private final ReportPhotoService reportPhotoService;

    @PostMapping("/stages/{stageId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать отчет по этапу строительства",
            description = "Создает новый отчет для указанного этапа строительства")
    public StageReportResponseDto createReport(
            @PathVariable UUID stageId,
            @Valid @RequestBody StageReportCreateDto createDto) {
        return stageReportService.createReport(stageId, createDto);
    }

    @GetMapping("/stages/{stageId}")
    @Operation(summary = "Получить все отчеты по этапу",
            description = "Возвращает список всех отчетов для указанного этапа строительства")
    public List<StageReportResponseDto> getReportsByStage(@PathVariable UUID stageId) {
        return stageReportService.getReportsByStage(stageId);
    }

    @GetMapping("/stages/{stageId}/published")
    @Operation(summary = "Получить опубликованные отчеты по этапу",
            description = "Возвращает только опубликованные отчеты для указанного этапа")
    public List<StageReportResponseDto> getPublishedReportsByStage(@PathVariable UUID stageId) {
        return stageReportService.getPublishedReportsByStage(stageId);
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Получить отчет по ID",
            description = "Возвращает детальную информацию об отчете")
    public StageReportResponseDto getReportById(@PathVariable UUID reportId) {
        return stageReportService.getReportById(reportId);
    }

    @GetMapping("/stages/{stageId}/or-create")
    @Operation(summary = "Получить или создать отчет",
            description = "Возвращает существующий отчет или создает новый черновик")
    public StageReportResponseDto getOrCreateReport(@PathVariable UUID stageId) {
        return stageReportService.getOrCreateReport(stageId);
    }

    @PatchMapping("/{reportId}/publish")
    @Operation(summary = "Опубликовать отчет",
            description = "Изменяет статус отчета на PUBLISHED")
    public StageReportResponseDto publishReport(@PathVariable UUID reportId) {
        return stageReportService.publishReport(reportId);
    }

    @PutMapping("/{reportId}")
    @Operation(summary = "Обновить отчет",
            description = "Обновляет описание отчета")
    public StageReportResponseDto updateReport(
            @PathVariable UUID reportId,
            @RequestParam String description) {
        return stageReportService.updateReport(reportId, description);
    }

    @DeleteMapping("/{reportId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить отчет",
            description = "Удаляет отчет и все связанные фотографии")
    public void deleteReport(@PathVariable UUID reportId) {
        stageReportService.deleteReport(reportId);
    }

    @GetMapping("/stages/{stageId}/has-published")
    @Operation(summary = "Проверить наличие опубликованного отчета",
            description = "Возвращает true если для этапа есть опубликованный отчет")
    public boolean hasPublishedReport(@PathVariable UUID stageId) {
        return stageReportService.hasPublishedReport(stageId);
    }

    @PostMapping("/{reportId}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить фотографию к отчету",
            description = "Загружает фотографию и привязывает ее к отчету об этапе")
    public ReportPhotoResponseDto addPhotoToReport(
            @PathVariable UUID reportId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) throws IOException {
        return reportPhotoService.addPhotoToReport(reportId, file, description);
    }

    @GetMapping("/{reportId}/photos")
    @Operation(summary = "Получить фотографии отчета",
            description = "Возвращает все фотографии, привязанные к отчету")
    public List<ReportPhotoResponseDto> getReportPhotos(@PathVariable UUID reportId) {
        return reportPhotoService.getPhotosByReport(reportId);
    }

    @DeleteMapping("/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить фотографию отчета",
            description = "Удаляет фотографию из отчета")
    public void deleteReportPhoto(@PathVariable UUID photoId) {
        reportPhotoService.deleteReportPhoto(photoId);
    }
}