package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import dev.msi_hackaton.backend_app.dto.request.DocumentUploadDto;
import dev.msi_hackaton.backend_app.dto.response.DocumentResponseDto;
import dev.msi_hackaton.backend_app.service.DocumentService;
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
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/constructions/{constructionId}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Загрузить документ для этапа строительства")
    public DocumentResponseDto uploadDocument(
            @PathVariable UUID constructionId,
            @Valid @RequestPart("metadata") DocumentUploadDto uploadDto,
            @RequestPart("file") MultipartFile file) throws IOException {
        return documentService.uploadDocument(constructionId, uploadDto, file);
    }

    @GetMapping("/constructions/{constructionId}")
    @Operation(summary = "Получить все документы этапа строительства")
    public List<DocumentResponseDto> getDocumentsByConstruction(@PathVariable UUID constructionId) {
        return documentService.getDocumentsByConstruction(constructionId);
    }

    @GetMapping("/constructions/{constructionId}/status/{status}")
    @Operation(summary = "Получить документы по статусу")
    public List<DocumentResponseDto> getDocumentsByStatus(
            @PathVariable UUID constructionId,
            @PathVariable DocumentStatus status) {
        return documentService.getDocumentsByStatus(constructionId, status);
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Получить документ по ID")
    public DocumentResponseDto getDocumentById(@PathVariable UUID documentId) {
        return documentService.getDocumentById(documentId);
    }

    @PatchMapping("/{documentId}/status")
    @Operation(summary = "Обновить статус документа")
    public DocumentResponseDto updateDocumentStatus(
            @PathVariable UUID documentId,
            @RequestParam DocumentStatus status,
            @RequestParam(required = false) String comment) {
        return documentService.updateDocumentStatus(documentId, status, comment);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить документ")
    public void deleteDocument(@PathVariable UUID documentId) {
        documentService.deleteDocument(documentId);
    }
}