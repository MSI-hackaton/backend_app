package dev.msi_hackaton.backend_app.dao.entities;

import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ConstructionRequest request;

    @Column(name = "name", nullable = false)
    private String name;  // Название документа

    @Column(name = "file_url", nullable = false)
    private String fileUrl;  // Ссылка на файл в облачном хранилище

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "VARCHAR(20) DEFAULT 'UPLOADED' CHECK (status IN ('UPLOADED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'))"
    )
    private DocumentStatus status;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;
}
