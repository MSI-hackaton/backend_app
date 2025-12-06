package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.enums.RequestStatus;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionRequestRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dto.request.ConstructionRequestCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionRequestResponseDto;
import dev.msi_hackaton.backend_app.dto.response.ConstructionRequestStatusResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.ConstructionRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConstructionRequestService {
    private final ConstructionRequestRepository constructionRequestRepository;
    private final ProjectRepository projectRepository;
    private final ConstructionRequestMapper constructionRequestMapper;

    @Transactional
    public ConstructionRequestResponseDto createRequest(
            UUID projectId,
            ConstructionRequestCreateDto requestCreateDto) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        ConstructionRequest request = constructionRequestMapper.toEntity(requestCreateDto);
        request.setProject(project);
        request.setStatus(RequestStatus.PENDING);

        ConstructionRequest savedRequest = constructionRequestRepository.save(request);
        return constructionRequestMapper.toDto(savedRequest);
    }

    @Transactional(readOnly = true)
    public ConstructionRequestStatusResponseDto getRequestStatus(UUID requestId) {
        ConstructionRequest request = constructionRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Construction request not found with id: " + requestId));

        return constructionRequestMapper.toStatusDto(request);
    }
}
