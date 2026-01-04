package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.VideoStreamCreateDto;
import dev.msi_hackaton.backend_app.dto.response.VideoStreamResponseDto;
import dev.msi_hackaton.backend_app.service.VideoStreamService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/video-streams")
@RequiredArgsConstructor
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    @PostMapping("/requests/{requestId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить видеопоток к заявке",
            description = "Создаёт новую камеру видеонаблюдения для строительной площадки")
    public VideoStreamResponseDto createStream(
            @PathVariable UUID requestId,
            @Valid @RequestBody VideoStreamCreateDto createDto) {
        return videoStreamService.createVideoStream(requestId, createDto);
    }

    @GetMapping("/requests/{requestId}")
    @Operation(summary = "Получить все видеопотоки заявки",
            description = "Возвращает список всех камер для данной строительной площадки")
    public List<VideoStreamResponseDto> getStreamsByRequest(@PathVariable UUID requestId) {
        return videoStreamService.getStreamsByRequest(requestId);
    }

    @GetMapping("/{streamId}")
    @Operation(summary = "Получить информацию о видеопотоке",
            description = "Возвращает детальную информацию о конкретной камере")
    public VideoStreamResponseDto getStream(@PathVariable UUID streamId) {
        return videoStreamService.getStreamById(streamId);
    }

    @PutMapping("/{streamId}")
    @Operation(summary = "Обновить видеопоток",
            description = "Обновляет параметры камеры (URL, название и т.д.)")
    public VideoStreamResponseDto updateStream(
            @PathVariable UUID streamId,
            @Valid @RequestBody VideoStreamCreateDto updateDto) {
        return videoStreamService.updateStream(streamId, updateDto);
    }

    @PatchMapping("/{streamId}/toggle")
    @Operation(summary = "Переключить статус видеопотока",
            description = "Включает/выключает трансляцию с камеры")
    public void toggleStreamStatus(@PathVariable UUID streamId) {
        videoStreamService.toggleStreamStatus(streamId);
    }

    @DeleteMapping("/{streamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить видеопоток",
            description = "Удаляет камеру видеонаблюдения")
    public void deleteStream(@PathVariable UUID streamId) {
        videoStreamService.deleteStream(streamId);
    }
}