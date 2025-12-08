package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "photos")
public class Photo extends AbstractEntity {

    @Column(name = "url", nullable = false)
    private String url;

//    @Column(name = "file_type")
//    private String fileType;  // MIME-тип (например, "image/jpeg")
//
//    @Column(name = "storage_path", nullable = false)
//    private String storagePath;  // Путь в хранилище (например, "photos/123.jpg")
}