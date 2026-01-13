package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ReportPhoto;
import dev.msi_hackaton.backend_app.dao.entities.StageReport;
import dev.msi_hackaton.backend_app.dao.repository.ReportPhotoRepository;
import dev.msi_hackaton.backend_app.dao.repository.StageReportRepository;
import dev.msi_hackaton.backend_app.dto.response.ReportPhotoResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.ReportPhotoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportPhotoService {

    private final ReportPhotoRepository reportPhotoRepository;
    private final StageReportRepository stageReportRepository;
    private final StorageService storageService;
    private final ReportPhotoMapper reportPhotoMapper;

    @Transactional
    public ReportPhotoResponseDto addPhotoToReport(UUID reportId, MultipartFile file, String description) throws IOException {
        StageReport report = stageReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Stage report not found: " + reportId));

        // Загружаем файл в хранилище
        String fileUrl = storageService.uploadFile(file);

        // Создаем сущность ReportPhoto
        ReportPhoto reportPhoto = new ReportPhoto();
        reportPhoto.setReport(report);
        reportPhoto.setUrl(fileUrl);
        reportPhoto.setDescription(description);

        ReportPhoto saved = reportPhotoRepository.save(reportPhoto);
        return reportPhotoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReportPhotoResponseDto> getPhotosByReport(UUID reportId) {
        List<ReportPhoto> photos = reportPhotoRepository.findByReportId(reportId);
        return photos.stream()
                .map(reportPhotoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportPhotoResponseDto getPhotoById(UUID photoId) {
        ReportPhoto photo = reportPhotoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Report photo not found: " + photoId));
        return reportPhotoMapper.toDto(photo);
    }

    @Transactional
    public void deleteReportPhoto(UUID photoId) {
        ReportPhoto photo = reportPhotoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Report photo not found: " + photoId));

        // Удаляем файл из хранилища
        storageService.deleteFile(photo.getUrl());

        // Удаляем запись из БД
        reportPhotoRepository.delete(photo);
    }

    @Transactional
    public void deleteAllPhotosByReport(UUID reportId) {
        List<ReportPhoto> photos = reportPhotoRepository.findByReportId(reportId);

        // Удаляем файлы из хранилища
        photos.forEach(photo -> storageService.deleteFile(photo.getUrl()));

        // Удаляем записи из БД
        reportPhotoRepository.deleteAll(photos);
    }
}