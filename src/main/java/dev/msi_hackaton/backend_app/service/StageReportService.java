package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.StageReport;
import dev.msi_hackaton.backend_app.dao.entities.enums.ReportStatus;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.dao.repository.StageReportRepository;
import dev.msi_hackaton.backend_app.dto.request.StageReportCreateDto;
import dev.msi_hackaton.backend_app.dto.response.StageReportResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.StageReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StageReportService {

    private final StageReportRepository stageReportRepository;
    private final ConstructionStageRepository constructionStageRepository;
    private final ReportPhotoService reportPhotoService;
    private final StageReportMapper stageReportMapper;

    @Transactional
    public StageReportResponseDto createReport(UUID stageId, StageReportCreateDto createDto) {
        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + stageId));

        // Проверяем, не существует ли уже отчет для этого этапа
        List<StageReport> existingReports = stageReportRepository.findByStageId(stageId);
        if (!existingReports.isEmpty()) {
            // Можно либо обновить существующий, либо создать новый
            // В этом случае обновим первый найденный
            StageReport existingReport = existingReports.get(0);
            existingReport.setDescription(createDto.getDescription());
            existingReport.setStatus(ReportStatus.DRAFT);
            StageReport updated = stageReportRepository.save(existingReport);
            return stageReportMapper.toDto(updated);
        }

        // Создаем новый отчет
        StageReport report = stageReportMapper.toEntity(createDto);
        report.setStage(stage);
        report.setStatus(ReportStatus.DRAFT);

        StageReport saved = stageReportRepository.save(report);
        return stageReportMapper.toDto(saved);
    }

    @Transactional
    public StageReportResponseDto publishReport(UUID reportId) {
        StageReport report = stageReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found: " + reportId));

        if (report.getStatus() != ReportStatus.PUBLISHED) {
            report.setStatus(ReportStatus.PUBLISHED);
            StageReport updated = stageReportRepository.save(report);
            return stageReportMapper.toDto(updated);
        }

        return stageReportMapper.toDto(report);
    }

    @Transactional
    public StageReportResponseDto updateReport(UUID reportId, String description) {
        StageReport report = stageReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found: " + reportId));

        report.setDescription(description);
        StageReport updated = stageReportRepository.save(report);
        return stageReportMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    public List<StageReportResponseDto> getReportsByStage(UUID stageId) {
        List<StageReport> reports = stageReportRepository.findByStageId(stageId);
        return reports.stream()
                .map(stageReportMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StageReportResponseDto getReportById(UUID reportId) {
        StageReport report = stageReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found: " + reportId));
        return stageReportMapper.toDto(report);
    }

    @Transactional(readOnly = true)
    public List<StageReportResponseDto> getPublishedReportsByStage(UUID stageId) {
        List<StageReport> reports = stageReportRepository.findByStageId(stageId);
        return reports.stream()
                .filter(report -> report.getStatus() == ReportStatus.PUBLISHED)
                .map(stageReportMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteReport(UUID reportId) {
        StageReport report = stageReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found: " + reportId));

        // Удаляем все фотографии отчета
        reportPhotoService.deleteAllPhotosByReport(reportId);

        // Удаляем сам отчет
        stageReportRepository.delete(report);
    }

    @Transactional(readOnly = true)
    public boolean hasPublishedReport(UUID stageId) {
        List<StageReport> reports = stageReportRepository.findByStageId(stageId);
        return reports.stream()
                .anyMatch(report -> report.getStatus() == ReportStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public StageReportResponseDto getOrCreateReport(UUID stageId) {
        List<StageReport> reports = stageReportRepository.findByStageId(stageId);
        if (!reports.isEmpty()) {
            return stageReportMapper.toDto(reports.get(0));
        }

        // Создаем новый черновик отчета
        StageReportCreateDto createDto = new StageReportCreateDto();
        createDto.setDescription("Отчет по этапу строительства");

        ConstructionStage stage = constructionStageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + stageId));

        StageReport report = new StageReport();
        report.setStage(stage);
        report.setDescription(createDto.getDescription());
        report.setStatus(ReportStatus.DRAFT);

        StageReport saved = stageReportRepository.save(report);
        return stageReportMapper.toDto(saved);
    }
}