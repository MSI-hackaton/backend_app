package dev.msi_hackaton.backend_app.dao.entities;

import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentAgreementStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "document_agreements")
public class DocumentAgreement extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_request_id", nullable = false)
    private ConstructionRequest constructionRequest;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "documentAgreement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgreementDocument> agreementDocuments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentAgreementStatus status = DocumentAgreementStatus.PENDING_REVIEW;

    @Column(name = "required_signatures")
    private Boolean requiredSignatures = true;

    @Column(name = "deadline")
    private Instant deadline;

    @Column(name = "signed_at")
    private Instant signedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_by")
    private User signedBy;

    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData; // Для электронной подписи
}