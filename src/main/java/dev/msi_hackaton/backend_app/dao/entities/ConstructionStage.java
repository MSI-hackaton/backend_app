package dev.msi_hackaton.backend_app.dao.entities;

import dev.msi_hackaton.backend_app.dao.entities.enums.StageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "construction_stages")
public class ConstructionStage extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ConstructionRequest request;

    @Column(name = "name", nullable = false)
    private String name;  // Название этапа (например, "Фундамент")

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;  // Описание этапа

    @Column(name = "start_date")
    private Instant startDate;  // Дата начала этапа

    @Column(name = "end_date")
    private Instant endDate;  // Дата окончания этапа

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "VARCHAR(20) DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED'))"
    )
    private StageStatus status;
}
