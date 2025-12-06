package dev.msi_hackaton.backend_app.dao.entities;

import dev.msi_hackaton.backend_app.dao.entities.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "area")
    private Double area;

    @Column(name = "floors")
    private Integer floors;

    @Column(name = "price")
    private Double price;

    @Column(name = "construction_time")
    private Integer constructionTime;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'UNDER_CONSTRUCTION', 'COMPLETED'))"
    )
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPhoto> projectPhotos = new ArrayList<>();
}
