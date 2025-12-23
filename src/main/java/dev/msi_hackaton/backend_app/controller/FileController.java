package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileStorageService.upload(file));
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> download(@PathVariable String name) {
        byte[] file = fileStorageService.download(name);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name)
                .body(file);
    }

}
