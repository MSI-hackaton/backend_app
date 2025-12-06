package dev.msi_hackaton.backend_app.dao.entities;

import dev.msi_hackaton.backend_app.dao.entities.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "construction_requests")
public class ConstructionRequest extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            columnDefinition = "VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))"
    )
    private RequestStatus status;

    @Column(name = "anonymous_full_name")
    private String anonymousFullName;

    @Column(name = "anonymous_email")
    private String anonymousEmail;

    @Column(name = "anonymous_phone")
    private String anonymousPhone;
}
