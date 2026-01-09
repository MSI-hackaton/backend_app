package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "document_templates")
public class DocumentTemplate extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "required", nullable = false)
    private Boolean required = true;

    @Column(name = "category")
    private String category; // "Строительная документация", "Юридические документы" и т.д.
}