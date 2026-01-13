package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.enums.StageStatus;
import dev.msi_hackaton.backend_app.dto.request.ConstructionStageCreateDto;
import dev.msi_hackaton.backend_app.dto.request.ConstructionStageUpdateDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionStageResponseDto;
import dev.msi_hackaton.backend_app.mapper.ConstructionStageMapper;
import dev.msi_hackaton.backend_app.service.ConstructionStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/construction-stages")
@RequiredArgsConstructor
public class ConstructionStageController {

    private final ConstructionStageService constructionStageService;
    private final ConstructionStageMapper constructionStageMapper;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ConstructionStageResponseDto>> getStagesByProject(@PathVariable UUID projectId) {
        List<ConstructionStage> stages = constructionStageService.getAllStagesByProjectId(projectId);
        return ResponseEntity.ok(stages.stream()
                .map(constructionStageMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ConstructionStageResponseDto>> getStagesByCustomer(@PathVariable UUID customerId) {
        List<ConstructionStage> stages = constructionStageService.getAllStagesByCustomerId(customerId);
        return ResponseEntity.ok(stages.stream()
                .map(constructionStageMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<List<ConstructionStageResponseDto>> getStagesBySpecialist(@PathVariable UUID specialistId) {
        List<ConstructionStage> stages = constructionStageService.getAllStagesBySpecialistId(specialistId);
        return ResponseEntity.ok(stages.stream()
                .map(constructionStageMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConstructionStageResponseDto> getStageById(@PathVariable UUID id) {
        ConstructionStage stage = constructionStageService.getStageById(id);
        return ResponseEntity.ok(constructionStageMapper.toResponseDto(stage));
    }

    @PostMapping
    public ResponseEntity<ConstructionStageResponseDto> createStage(@RequestBody ConstructionStageCreateDto dto) {
        ConstructionStage stage = constructionStageService.createStage(
                dto.getRequestId(),
                dto.getProjectId(),
                dto.getCustomerId(),
                dto.getName(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(constructionStageMapper.toResponseDto(stage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConstructionStageResponseDto> updateStage(
            @PathVariable UUID id,
            @RequestBody ConstructionStageUpdateDto dto) {
        ConstructionStage stage = constructionStageService.updateStage(
                id,
                dto.getName(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getStatus()
        );
        return ResponseEntity.ok(constructionStageMapper.toResponseDto(stage));
    }

    @PutMapping("/{id}/specialist/{specialistId}")
    public ResponseEntity<ConstructionStageResponseDto> assignSpecialist(
            @PathVariable UUID id,
            @PathVariable UUID specialistId) {
        ConstructionStage stage = constructionStageService.assignSpecialist(id, specialistId);
        return ResponseEntity.ok(constructionStageMapper.toResponseDto(stage));
    }

    @DeleteMapping("/{id}/specialist")
    public ResponseEntity<ConstructionStageResponseDto> removeSpecialist(@PathVariable UUID id) {
        ConstructionStage stage = constructionStageService.removeSpecialist(id);
        return ResponseEntity.ok(constructionStageMapper.toResponseDto(stage));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ConstructionStageResponseDto> updateStatus(
            @PathVariable UUID id,
            @RequestParam StageStatus status) {
        ConstructionStage stage = constructionStageService.updateStatus(id, status);
        return ResponseEntity.ok(constructionStageMapper.toResponseDto(stage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable UUID id) {
        constructionStageService.deleteStage(id);
        return ResponseEntity.noContent().build();
    }
}
