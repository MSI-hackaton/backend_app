package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ConstructionRequestCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionRequestResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionRequestStatusResponseDto;
import dev.msi_hackaton.backend_app.service.ConstructionRequestService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ConstructionRequestController {
    private final ConstructionRequestService constructionRequestService;

    @PostMapping("/projects/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать заявку на строительство",
            description = "Создаёт новую заявку на строительство по выбранному проекту и возвращает данные заявки."
    )
    public ConstructionRequestResponseDto createRequest(
            @PathVariable UUID projectId,
            @Valid @RequestBody ConstructionRequestCreateDto requestCreateDto) {
        return constructionRequestService.createRequest(projectId, requestCreateDto);
    }

    @GetMapping("/{requestId}/status")
    @Operation(
            summary = "Получить статус заявки",
            description = "Возвращает текущий статус заявки на строительство по её идентификатору."
    )
    public ConstructionRequestStatusResponseDto getRequestStatus(@PathVariable UUID requestId) {
        return constructionRequestService.getRequestStatus(requestId);
    }
}
