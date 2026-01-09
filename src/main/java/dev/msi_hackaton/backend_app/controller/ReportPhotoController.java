package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.response.ReportPhotoResponseDto;
import dev.msi_hackaton.backend_app.service.ReportPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/report-photos")
@RequiredArgsConstructor
public class ReportPhotoController {

    private final ReportPhotoService reportPhotoService;

    @PostMapping("/reports/{reportId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить фотографию к отчету",
            description = "Загружает фотографию и привязывает ее к отчету об этапе строительства")
    public ReportPhotoResponseDto addPhotoToReport(
            @PathVariable UUID reportId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) throws IOException {
        return reportPhotoService.addPhotoToReport(reportId, file, description);
    }

    @GetMapping("/reports/{reportId}")
    @Operation(summary = "Получить фотографии отчета",
            description = "Возвращает все фотографии, привязанные к отчету")
    public List<ReportPhotoResponseDto> getPhotosByReport(@PathVariable UUID reportId) {
        return reportPhotoService.getPhotosByReport(reportId);
    }

    @GetMapping("/{photoId}")
    @Operation(summary = "Получить фотографию по ID",
            description = "Возвращает информацию о конкретной фотографии отчета")
    public ReportPhotoResponseDto getPhotoById(@PathVariable UUID photoId) {
        return reportPhotoService.getPhotoById(photoId);
    }

    @DeleteMapping("/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить фотографию отчета",
            description = "Удаляет фотографию отчета и файл из хранилища")
    public void deleteReportPhoto(@PathVariable UUID photoId) {
        reportPhotoService.deleteReportPhoto(photoId);
    }
}