package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.response.PhotoResponseDto;
import dev.msi_hackaton.backend_app.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;

    @GetMapping
    @Operation(
            summary = "Получить список фотографий",
            description = "Возвращает список всех загруженных фотографий"
    )
    public List<PhotoResponseDto> getAllPhotos() {
        return photoService.getAllPhotos();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить фотографию по ID",
            description = "Возвращает информацию о фотографии по её идентификатору"
    )
    public PhotoResponseDto getPhotoById(@PathVariable UUID id) {
        return photoService.getPhotoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Загрузить фотографию",
            description = "Загружает файл изображения и создаёт сущность фотографии"
    )
    public PhotoResponseDto uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        return photoService.uploadPhoto(file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить фотографию",
            description = "Удаляет фотографию по идентификатору"
    )
    public void deletePhoto(@PathVariable UUID id) {
        photoService.deletePhoto(id);
    }
}
