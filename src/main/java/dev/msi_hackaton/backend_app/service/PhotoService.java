package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.Photo;
import dev.msi_hackaton.backend_app.dao.repository.PhotoRepository;
import dev.msi_hackaton.backend_app.dto.request.PhotoUploadDto;
import dev.msi_hackaton.backend_app.dto.response.PhotoResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.PhotoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final StorageService storageService; // Сервис для работы с облачным хранилищем

    @Transactional(readOnly = true)
    public List<PhotoResponseDto> getAllPhotos() {
        return photoRepository.findAll().stream()
                .map(photoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PhotoResponseDto getPhotoById(UUID id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + id));
        return photoMapper.toDto(photo);
    }

    @Transactional
    public PhotoResponseDto uploadPhoto(MultipartFile file) throws IOException {
        // Загружаем файл в облачное хранилище
        String fileUrl = storageService.uploadFile(file);

        // Создаём сущность Photo
        Photo photo = new Photo();
        photo.setUrl(fileUrl);

        // Сохраняем в базу данных
        Photo savedPhoto = photoRepository.save(photo);
        return photoMapper.toDto(savedPhoto);
    }

    @Transactional
    public void deletePhoto(UUID id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found with id: " + id));

        // Удаляем файл из облачного хранилища
        storageService.deleteFile(photo.getUrl());

        // Удаляем запись из базы данных
        photoRepository.delete(photo);
    }
}
