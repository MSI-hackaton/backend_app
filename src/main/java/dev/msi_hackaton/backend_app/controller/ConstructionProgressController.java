package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.enums.StageStatus;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.dto.response.StageProgressDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/constructions")
@RequiredArgsConstructor
public class ConstructionProgressController {

    private final ConstructionStageRepository constructionStageRepository;

    @GetMapping("/{constructionId}/current-stage")
    @Operation(summary = "Получить текущий этап строительства",
            description = "Возвращает информацию о текущем активном этапе строительства")
    public ConstructionStageDto getCurrentStage(@PathVariable UUID constructionId) {
        ConstructionStage stage = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + constructionId));

        return mapToDto(stage);
    }

    @GetMapping("/{constructionId}/stage-progress")
    @Operation(summary = "Получить прогресс этапов",
            description = "Возвращает информацию о прогрессе всех этапов строительства")
    public StageProgressDto getStageProgress(@PathVariable UUID constructionId) {
        ConstructionStage mainStage = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + constructionId));

        UUID requestId = mainStage.getRequest().getId();
        List<ConstructionStage> allStages = constructionStageRepository.findByRequestId(requestId);

        return calculateStageProgress(allStages);
    }

    @GetMapping("/{constructionId}/all-stages")
    @Operation(summary = "Получить все этапы строительства",
            description = "Возвращает список всех этапов строительства")
    public List<ConstructionStageDto> getAllStages(@PathVariable UUID constructionId) {
        ConstructionStage mainStage = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + constructionId));

        UUID requestId = mainStage.getRequest().getId();
        List<ConstructionStage> allStages = constructionStageRepository.findByRequestId(requestId);

        return allStages.stream()
                .map(this::mapToDto)
                .toList();
    }

    @GetMapping("/{constructionId}/next-stage")
    @Operation(summary = "Получить следующий этап",
            description = "Возвращает информацию о следующем запланированном этапе")
    public ConstructionStageDto getNextStage(@PathVariable UUID constructionId) {
        ConstructionStage mainStage = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found: " + constructionId));

        UUID requestId = mainStage.getRequest().getId();
        List<ConstructionStage> allStages = constructionStageRepository.findByRequestId(requestId);

        ConstructionStage currentStage = allStages.stream()
                .filter(stage -> stage.getStatus() == StageStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);

        if (currentStage == null) {
            return allStages.stream()
                    .filter(stage -> stage.getStatus() == StageStatus.PLANNED)
                    .findFirst()
                    .map(this::mapToDto)
                    .orElse(null);
        }

        return allStages.stream()
                .filter(stage -> stage.getStartDate() != null)
                .filter(stage -> stage.getStartDate().isAfter(currentStage.getStartDate()))
                .filter(stage -> stage.getStatus() == StageStatus.PLANNED)
                .min((s1, s2) -> s1.getStartDate().compareTo(s2.getStartDate()))
                .map(this::mapToDto)
                .orElse(null);
    }

    private ConstructionStageDto mapToDto(ConstructionStage stage) {
        ConstructionStageDto dto = new ConstructionStageDto();
        dto.setId(stage.getId());
        dto.setName(stage.getName());
        dto.setDescription(stage.getDescription());
        dto.setStatus(stage.getStatus());
        dto.setStartDate(stage.getStartDate());
        dto.setEndDate(stage.getEndDate());

        if (stage.getStartDate() != null && stage.getEndDate() != null) {
            long totalDuration = Duration.between(stage.getStartDate(), stage.getEndDate()).toDays();
            if (totalDuration > 0) {
                long daysPassed = Duration.between(stage.getStartDate(), Instant.now()).toDays();
                dto.setProgressPercentage(Math.min(100, Math.max(0, (int) ((daysPassed * 100) / totalDuration))));
            }
        }

        return dto;
    }

    private StageProgressDto calculateStageProgress(List<ConstructionStage> stages) {
        StageProgressDto progress = new StageProgressDto();
        progress.setTotalStages(stages.size());

        long completed = stages.stream()
                .filter(stage -> stage.getStatus() == StageStatus.COMPLETED)
                .count();
        long inProgress = stages.stream()
                .filter(stage -> stage.getStatus() == StageStatus.IN_PROGRESS)
                .count();
        long planned = stages.stream()
                .filter(stage -> stage.getStatus() == StageStatus.PLANNED)
                .count();

        progress.setCompletedStages(completed);
        progress.setInProgressStages(inProgress);
        progress.setPlannedStages(planned);

        if (stages.size() > 0) {
            progress.setOverallProgress((int) ((completed * 100) / stages.size()));
        }

        stages.stream()
                .filter(stage -> stage.getStatus() == StageStatus.IN_PROGRESS)
                .findFirst()
                .ifPresent(stage -> progress.setCurrentStageName(stage.getName()));

        return progress;
    }

    public static class ConstructionStageDto {
        private UUID id;
        private String name;
        private String description;
        private StageStatus status;
        private Instant startDate;
        private Instant endDate;
        private Integer progressPercentage;

        // Геттеры и сеттеры
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public StageStatus getStatus() { return status; }
        public void setStatus(StageStatus status) { this.status = status; }
        public Instant getStartDate() { return startDate; }
        public void setStartDate(Instant startDate) { this.startDate = startDate; }
        public Instant getEndDate() { return endDate; }
        public void setEndDate(Instant endDate) { this.endDate = endDate; }
        public Integer getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    }
}