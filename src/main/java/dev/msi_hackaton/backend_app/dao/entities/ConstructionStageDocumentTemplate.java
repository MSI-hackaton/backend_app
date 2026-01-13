package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "construction_stage_document_templates")
public class ConstructionStageDocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_stage_id", nullable = false)
    private ConstructionStage constructionStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_template_id", nullable = false)
    private DocumentTemplate documentTemplate;

    @Column(name = "sort_order")
    private Integer sortOrder;
}