package dev.msi_hackaton.backend_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    @Value("${storage.local.directory}")
    private String storageDirectory;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // Создаем директорию, если её нет
        Path storagePath = Paths.get(storageDirectory);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        // Генерируем уникальное имя файла
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = storagePath.resolve(fileName);

        // Сохраняем файл
        Files.copy(file.getInputStream(), filePath);

        // Возвращаем URL для доступа к файлу
        return "/storage/" + fileName;
    }

    @Override
    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(storageDirectory).resolve(fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileUrl, e);
        }
    }
}
