package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.VideoStreamCreateDto;
import dev.msi_hackaton.backend_app.dto.response.VideoSnapshotResponseDto;
import dev.msi_hackaton.backend_app.dto.response.VideoStreamResponseDto;
import dev.msi_hackaton.backend_app.service.VideoStreamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/video")
public class VideoStreamController {
    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }

    @GetMapping("/{requestId}/streams")
    @Operation(summary = "Получить список видеопотоков",
            description = "Возвращает доступные видеопотоки для заявки")
    public List<VideoStreamResponseDto> getStreams(@PathVariable UUID requestId) {
        return videoStreamService.getStreams(requestId);
    }

    @GetMapping("/{requestId}/stream")
    @Operation(summary = "Получить основной видеопоток",
            description = "Возвращает основной видеопоток для просмотра")
    public VideoStreamResponseDto getMainStream(@PathVariable UUID requestId) {
        return videoStreamService.getMainStream(requestId);
    }

    @PostMapping("/{requestId}/streams")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить видеопоток (админ)",
            description = "Добавляет новый видеопоток для заявки")
    public VideoStreamResponseDto addStream(
            @PathVariable UUID requestId,
            @RequestBody VideoStreamCreateDto streamDto) {
        return videoStreamService.addStream(requestId, streamDto);
    }

    @GetMapping("/{requestId}/snapshot")
    @Operation(summary = "Получить скриншот с камеры",
            description = "Возвращает текущий скриншот с камеры (эмуляция)")
    public VideoSnapshotResponseDto getSnapshot(@PathVariable UUID requestId) {
        return videoStreamService.getSnapshot(requestId);
    }
}