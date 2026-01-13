package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.Document;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.dao.repository.DocumentRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.request.DocumentUploadDto;
import dev.msi_hackaton.backend_app.dto.response.DocumentResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ConstructionStageRepository constructionStageRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final DocumentNotificationService notificationService;

    @Transactional
    public DocumentResponseDto uploadDocument(UUID constructionId, DocumentUploadDto uploadDto, MultipartFile file) throws IOException {
        ConstructionStage construction = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + constructionId));

        // Загружаем файл в хранилище
        String fileUrl = storageService.uploadFile(file);

        // Создаем документ
        Document document = new Document();
        document.setConstruction(construction);
        document.setName(uploadDto.getName());
        document.setDescription(uploadDto.getDescription());
        document.setFileUrl(fileUrl);
        document.setOriginalFilename(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document.setStatus(DocumentStatus.UPLOADED);

        Document saved = documentRepository.save(document);

        // Получаем текущего пользователя из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID uploadedByUserId = null;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(username);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByPhone(username);
            }
            if (userOptional.isPresent()) {
                uploadedByUserId = userOptional.get().getId();
            }
        }

        // Создаем уведомление о новом документе
        if (uploadedByUserId != null) {
            notificationService.createNewDocumentNotification(saved.getId(), uploadedByUserId);
        }

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDto> getDocumentsByConstruction(UUID constructionId) {
        return documentRepository.findByConstructionIdOrderByCreatedAt(constructionId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponseDto getDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));
        return mapToDto(document);
    }

    @Transactional
    public DocumentResponseDto updateDocumentStatus(UUID documentId, DocumentStatus status) {
        return updateDocumentStatus(documentId, status, null);
    }

    @Transactional
    public DocumentResponseDto updateDocumentStatus(UUID documentId, DocumentStatus status, String comment) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        DocumentStatus oldStatus = document.getStatus();
        document.setStatus(status);
        document.setReviewedAt(Instant.now());

        // Получаем текущего пользователя (кто изменил статус)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID changedByUserId = null;
        User reviewedBy = null;

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(username);
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByPhone(username);
            }
            if (userOptional.isPresent()) {
                changedByUserId = userOptional.get().getId();
                reviewedBy = userOptional.get();
                document.setReviewedBy(reviewedBy);
            }
        }

        Document updated = documentRepository.save(document);

        // Создаем уведомление об изменении статуса
        if (changedByUserId != null) {
            notificationService.createStatusChangeNotification(
                    documentId,
                    changedByUserId,
                    oldStatus,
                    status,
                    comment != null ? comment : "Статус изменен"
            );
        }

        return mapToDto(updated);
    }

    @Transactional
    public void deleteDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        // Удаляем файл из хранилища
        storageService.deleteFile(document.getFileUrl());

        // Удаляем запись из БД
        documentRepository.delete(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDto> getDocumentsByStatus(UUID constructionId, DocumentStatus status) {
        return documentRepository.findByConstructionIdAndStatus(constructionId, status).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDto> getDocumentsRequiringAttention(UUID constructionId) {
        return documentRepository.findByConstructionIdAndStatusIn(
                        constructionId,
                        List.of(DocumentStatus.UPLOADED, DocumentStatus.UNDER_REVIEW)
                ).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentStatusSummary getDocumentStatusSummary(UUID constructionId) {
        List<Document> documents = documentRepository.findByConstructionIdOrderByCreatedAt(constructionId);

        long total = documents.size();
        long uploaded = documents.stream().filter(d -> d.getStatus() == DocumentStatus.UPLOADED).count();
        long underReview = documents.stream().filter(d -> d.getStatus() == DocumentStatus.UNDER_REVIEW).count();
        long approved = documents.stream().filter(d -> d.getStatus() == DocumentStatus.APPROVED).count();
        long rejected = documents.stream().filter(d -> d.getStatus() == DocumentStatus.REJECTED).count();

        return new DocumentStatusSummary(total, uploaded, underReview, approved, rejected);
    }

    @Transactional
    public DocumentResponseDto updateDocumentInfo(UUID documentId, String name, String description) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        if (name != null && !name.trim().isEmpty()) {
            document.setName(name);
        }

        if (description != null) {
            document.setDescription(description);
        }

        Document updated = documentRepository.save(document);
        return mapToDto(updated);
    }

    private DocumentResponseDto mapToDto(Document document) {
        DocumentResponseDto dto = new DocumentResponseDto();
        dto.setId(document.getId());
        dto.setConstructionId(document.getConstruction().getId());
        dto.setName(document.getName());
        dto.setDescription(document.getDescription());
        dto.setFileUrl(document.getFileUrl());
        dto.setOriginalFilename(document.getOriginalFilename());
        dto.setFileSize(document.getFileSize());
        dto.setMimeType(document.getMimeType());
        dto.setStatus(document.getStatus());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setReviewedAt(document.getReviewedAt());

        if (document.getReviewedBy() != null) {
            dto.setReviewedById(document.getReviewedBy().getId());
            dto.setReviewedByName(document.getReviewedBy().getFullName());
        }

        return dto;
    }

    // Вспомогательный класс для сводки статусов
    @RequiredArgsConstructor
    @Getter
    public static class DocumentStatusSummary {
        private final long total;
        private final long uploaded;
        private final long underReview;
        private final long approved;
        private final long rejected;

        public double getCompletionPercentage() {
            if (total == 0) return 0.0;
            return (approved * 100.0) / total;
        }
    }
}