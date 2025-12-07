package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.DocumentSignRequest;
import dev.msi_hackaton.backend_app.dto.response.DocumentResponse;
import dev.msi_hackaton.backend_app.entity.Document;
import dev.msi_hackaton.backend_app.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Документы", description = "API для работы с документами")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Получить документы заказа")
    public ResponseEntity<List<DocumentResponse>> getOrderDocuments(@PathVariable Long orderId) {
        List<DocumentResponse> documents = documentService.getOrderDocuments(orderId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/order/{orderId}/pending")
    @Operation(summary = "Получить документы, ожидающие подписания")
    public ResponseEntity<List<DocumentResponse>> getPendingDocuments(@PathVariable Long orderId) {
        List<DocumentResponse> documents = documentService.getPendingDocuments(orderId);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/order/{orderId}/upload")
    @Operation(summary = "Загрузить документ")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long orderId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Document.DocumentType type) throws IOException {

        DocumentResponse document = documentService.uploadDocument(orderId, file, title, description, type);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/file/{fileName}")
    @Operation(summary = "Скачать документ по имени файла")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String fileName) throws IOException {
        byte[] fileContent = documentService.getDocumentFile(fileName);

        ByteArrayResource resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileContent.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/{documentId}/download")
    @Operation(summary = "Скачать документ по ID")
    public ResponseEntity<Resource> downloadDocumentById(@PathVariable Long documentId) throws IOException {
        byte[] fileContent = documentService.downloadDocument(documentId);

        ByteArrayResource resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document_" + documentId + ".pdf\"")
                .contentLength(fileContent.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @PostMapping("/{documentId}/request-sign")
    @Operation(summary = "Запросить подпись документа")
    public ResponseEntity<Void> requestSign(@PathVariable Long documentId) {
        documentService.sendSignRequest(documentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{documentId}/sign")
    @Operation(summary = "Подписать документ")
    public ResponseEntity<DocumentResponse> signDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentSignRequest request) {
        DocumentResponse document = documentService.signDocument(documentId, request);
        return ResponseEntity.ok(document);
    }

    @PostMapping("/order/{orderId}/archive")
    @Operation(summary = "Скачать архив всех документов заказа")
    public ResponseEntity<Resource> downloadArchive(@PathVariable Long orderId) throws IOException {
        byte[] archiveData = documentService.generateArchive(orderId);

        ByteArrayResource resource = new ByteArrayResource(archiveData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documents_" + orderId + ".zip\"")
                .contentLength(archiveData.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/order/{orderId}/create-final")
    @Operation(summary = "Создать финальные документы для заказа")
    public ResponseEntity<Void> createFinalDocuments(@PathVariable Long orderId) {
        // В реальном приложении здесь должна быть проверка прав и статуса заказа
        return ResponseEntity.ok().build();
    }
}