package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.DocumentAgreementCreateDto;
import dev.msi_hackaton.backend_app.dto.response.DocumentAgreementResponseDto;
import dev.msi_hackaton.backend_app.service.DocumentAgreementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/document-agreements")
@RequiredArgsConstructor
@Tag(name = "Document Agreements", description = "API для согласования документации")
public class DocumentAgreementController {

    private final DocumentAgreementService documentAgreementService;

    @PostMapping("/requests/{requestId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать документ для согласования",
            description = "Создаёт новый документ для согласования по заявке на строительство"
    )
    public DocumentAgreementResponseDto createDocumentAgreement(
            @Parameter(description = "ID заявки на строительство", required = true)
            @PathVariable UUID requestId,

            @Parameter(description = "Данные документа для согласования", required = true)
            @Valid @RequestBody DocumentAgreementCreateDto createDto) {

        return documentAgreementService.createDocumentAgreement(requestId, createDto);
    }

    @GetMapping("/requests/{requestId}")
    @Operation(
            summary = "Получить все документы для согласования по заявке",
            description = "Возвращает список всех документов для согласования по заявке на строительство"
    )
    public List<DocumentAgreementResponseDto> getAgreementsByRequest(
            @Parameter(description = "ID заявки на строительство", required = true)
            @PathVariable UUID requestId) {

        return documentAgreementService.getAgreementsByRequest(requestId);
    }

    @PatchMapping("/{agreementId}/status")
    @Operation(
            summary = "Обновить статус согласования документа",
            description = "Изменяет статус согласования документа (PENDING_REVIEW, UNDER_REVIEW, etc.)"
    )
    public DocumentAgreementResponseDto updateStatus(
            @Parameter(description = "ID документа для согласования", required = true)
            @PathVariable UUID agreementId,

            @Parameter(description = "Новый статус", required = true)
            @RequestParam String status,

            @Parameter(description = "Комментарий к изменению статуса")
            @RequestParam(required = false) String comment) {

        return documentAgreementService.updateStatus(agreementId, status, comment);
    }

    @PostMapping("/{agreementId}/sign")
    @Operation(
            summary = "Подписать документ",
            description = "Электронное подписание документа согласования"
    )
    public DocumentAgreementResponseDto signDocument(
            @Parameter(description = "ID документа для согласования", required = true)
            @PathVariable UUID agreementId,

            @Parameter(description = "Данные подписи")
            @RequestBody SignRequest signRequest) {

        return documentAgreementService.signDocument(
                agreementId,
                signRequest.getUserId(),
                signRequest.getSignatureData()
        );
    }

    @GetMapping("/{agreementId}")
    @Operation(
            summary = "Получить документ для согласования по ID",
            description = "Возвращает детальную информацию о документе для согласования"
    )
    public DocumentAgreementResponseDto getAgreementById(
            @Parameter(description = "ID документа для согласования", required = true)
            @PathVariable UUID agreementId) {

        return documentAgreementService.getAgreementById(agreementId);
    }

    @GetMapping("/users/{userId}/pending")
    @Operation(
            summary = "Получить документы, ожидающие действий пользователя",
            description = "Возвращает документы, которые требуют действий от указанного пользователя"
    )
    public List<DocumentAgreementResponseDto> getPendingAgreementsForUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable UUID userId) {

        return documentAgreementService.getPendingAgreementsForUser(userId);
    }

    @GetMapping("/checklist/requests/{requestId}")
    @Operation(
            summary = "Получить чек-лист документов для согласования",
            description = "Возвращает список всех необходимых документов для согласования по заявке"
    )
    public List<DocumentAgreementResponseDto> getDocumentChecklist(
            @Parameter(description = "ID заявки на строительство", required = true)
            @PathVariable UUID requestId) {

        return documentAgreementService.getDocumentChecklist(requestId);
    }
}

// Вспомогательный DTO для подписания
class SignRequest {
    private UUID userId;
    private String signatureData;

    // Геттеры и сеттеры
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getSignatureData() { return signatureData; }
    public void setSignatureData(String signatureData) { this.signatureData = signatureData; }
}