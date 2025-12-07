package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.entity.ConstructionStage;
import dev.msi_hackaton.backend_app.service.ConstructionStageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/construction")
@Tag(name = "Строительство", description = "API для отслеживания хода строительства")
public class ConstructionController {

    private final ConstructionStageService constructionStageService;

    public ConstructionController(ConstructionStageService constructionStageService) {
        this.constructionStageService = constructionStageService;
    }

    @GetMapping("/order/{orderId}/stages")
    @Operation(summary = "Получить этапы строительства заказа")
    public ResponseEntity<List<ConstructionStage>> getConstructionStages(@PathVariable Long orderId) {
        List<ConstructionStage> stages = constructionStageService.getOrderStages(orderId);
        return ResponseEntity.ok(stages);
    }

    @PatchMapping("/stages/{stageId}/progress")
    @Operation(summary = "Обновить прогресс этапа (админ)")
    public ResponseEntity<Void> updateStageProgress(
            @PathVariable Long stageId,
            @RequestParam Integer percentage) {
        constructionStageService.updateStageProgress(stageId, percentage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}/video-stream")
    @Operation(summary = "Получить URL видеопотока")
    public ResponseEntity<String> getVideoStreamUrl(@PathVariable Long orderId) {
        return ResponseEntity.ok("rtsp://stream.example.com/" + orderId);
    }
}