package dev.msi_hackaton.backend_app.dto.response;

import dev.msi_hackaton.backend_app.dao.entities.enums.RequestStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class ConstructionRequestStatusResponseDto {
    private UUID id;
    private RequestStatus status;
    private UUID projectId;
}
