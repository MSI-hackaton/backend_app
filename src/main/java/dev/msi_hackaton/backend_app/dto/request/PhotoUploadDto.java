package dev.msi_hackaton.backend_app.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PhotoUploadDto {
    private MultipartFile file;
    private String description;
}
