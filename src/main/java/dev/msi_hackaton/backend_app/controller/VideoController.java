package dev.msi_hackaton.backend_app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/video")
@Tag(name = "Видео", description = "API для работы с видеопотоками")
public class VideoController {

    @GetMapping("/order/{orderId}/stream")
    @Operation(summary = "Получить URL видеопотока для заказа")
    public ResponseEntity<Map<String, String>> getVideoStream(@PathVariable Long orderId) {
        // В реальном приложении здесь будет логика получения URL стрима
        // из базы данных или внешнего сервиса
        String streamUrl = "rtsp://stream.example.com/order/" + orderId;

        return ResponseEntity.ok(Map.of(
                "streamUrl", streamUrl,
                "type", "RTSP",
                "status", "ACTIVE"
        ));
    }

    @GetMapping("/order/{orderId}/recordings")
    @Operation(summary = "Получить список записей видеонаблюдения")
    public ResponseEntity<Map<String, Object>> getVideoRecordings(@PathVariable Long orderId) {
        // Заглушка для демонстрации
        return ResponseEntity.ok(Map.of(
                "recordings", new Object[]{
                        Map.of("id", 1, "date", "2024-01-15", "duration", "2h 30m", "url", "/recordings/1.mp4"),
                        Map.of("id", 2, "date", "2024-01-16", "duration", "3h 15m", "url", "/recordings/2.mp4")
                },
                "total", 2
        ));
    }
}