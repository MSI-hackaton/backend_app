package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.service.ConstructionStageService;
import dev.msi_hackaton.backend_app.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Тестовые данные", description = "API для создания тестовых данных")
public class TestDataController {

    private final OrderService orderService;
    private final ConstructionStageService constructionStageService;

    public TestDataController(OrderService orderService,
                              ConstructionStageService constructionStageService) {
        this.orderService = orderService;
        this.constructionStageService = constructionStageService;
    }

    @PostMapping("/order/{orderId}/stages")
    @Operation(summary = "Создать тестовые этапы строительства для заказа")
    public ResponseEntity<Void> createTestStages(@PathVariable Long orderId) {
        orderService.createConstructionStages(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{orderId}/progress/{stageNumber}/{percentage}")
    @Operation(summary = "Обновить прогресс конкретного этапа")
    public ResponseEntity<Void> updateStageProgress(
            @PathVariable Long orderId,
            @PathVariable Integer stageNumber,
            @PathVariable Integer percentage) {

        // Получаем все этапы заказа
        var stages = constructionStageService.getOrderStages(orderId);

        if (stages.size() >= stageNumber && stageNumber > 0) {
            var stage = stages.get(stageNumber - 1); // stageNumber начинается с 1
            constructionStageService.updateStageProgress(stage.getId(), percentage);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/order/{orderId}/start-construction")
    @Operation(summary = "Начать строительство заказа")
    public ResponseEntity<Void> startConstruction(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, dev.msi_hackaton.backend_app.entity.Order.OrderStatus.IN_PROGRESS);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{orderId}/complete-construction")
    @Operation(summary = "Завершить строительство заказа")
    public ResponseEntity<Void> completeConstruction(@PathVariable Long orderId) {
        // Устанавливаем все этапы на 100%
        var stages = constructionStageService.getOrderStages(orderId);
        for (var stage : stages) {
            constructionStageService.updateStageProgress(stage.getId(), 100);
        }

        // Меняем статус заказа
        orderService.updateOrderStatus(orderId, dev.msi_hackaton.backend_app.entity.Order.OrderStatus.COMPLETION);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{orderId}/finalize")
    @Operation(summary = "Завершить заказ полностью")
    public ResponseEntity<Void> finalizeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}/progress")
    @Operation(summary = "Получить прогресс заказа")
    public ResponseEntity<Double> getOrderProgress(@PathVariable Long orderId) {
        Double progress = orderService.getOrderProgress(orderId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/order/{orderId}/current-stage")
    @Operation(summary = "Получить текущий этап строительства")
    public ResponseEntity<String> getCurrentStage(@PathVariable Long orderId) {
        var stage = orderService.getCurrentStage(orderId);
        if (stage != null) {
            return ResponseEntity.ok(stage.getTitle() + " (" + stage.getCompletionPercentage() + "%)");
        } else {
            return ResponseEntity.ok("Этапы не созданы или строительство не начато");
        }
    }

    @PostMapping("/order/{orderId}/documents/sign-all")
    @Operation(summary = "Подписать все документы заказа (тестовый режим)")
    public ResponseEntity<Void> signAllDocuments(@PathVariable Long orderId) {
        // В тестовом режиме просто меняем статус заказа
        orderService.updateOrderStatus(orderId, dev.msi_hackaton.backend_app.entity.Order.OrderStatus.PREPARATION);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{orderId}/cancel")
    @Operation(summary = "Отменить заказ")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order/{orderId}/address")
    @Operation(summary = "Обновить адрес строительства")
    public ResponseEntity<Void> updateAddress(
            @PathVariable Long orderId,
            @RequestParam String address) {
        orderService.updateOrderAddress(orderId, address);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}/status")
    @Operation(summary = "Получить статус заказа")
    public ResponseEntity<String> getOrderStatus(@PathVariable Long orderId) {
        var order = orderService.getOrderEntity(orderId);
        return ResponseEntity.ok(order.getStatus().name());
    }

    @PostMapping("/order/{orderId}/simulate-progress")
    @Operation(summary = "Симулировать прогресс строительства")
    public ResponseEntity<Void> simulateProgress(@PathVariable Long orderId) {
        // Симуляция прогресса: постепенно увеличиваем процент выполнения каждого этапа
        var stages = constructionStageService.getOrderStages(orderId);

        for (int i = 0; i < stages.size(); i++) {
            var stage = stages.get(i);
            int progress = Math.min(100, (i + 1) * 15); // 15%, 30%, 45% и т.д.
            constructionStageService.updateStageProgress(stage.getId(), progress);
        }

        return ResponseEntity.ok().build();
    }
}