package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.Project;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.entities.enums.StageStatus;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionRequestRepository;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.dao.repository.ProjectRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConstructionStageService {

    private final ConstructionStageRepository constructionStageRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ConstructionRequestRepository constructionRequestRepository;

    @Transactional(readOnly = true)
    public List<ConstructionStage> getAllStagesByProjectId(UUID projectId) {
        return constructionStageRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public List<ConstructionStage> getAllStagesByCustomerId(UUID customerId) {
        return constructionStageRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<ConstructionStage> getAllStagesBySpecialistId(UUID specialistId) {
        return constructionStageRepository.findBySpecialistId(specialistId);
    }

    @Transactional(readOnly = true)
    public ConstructionStage getStageById(UUID id) {
        return constructionStageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Construction stage not found with id: " + id));
    }

    @Transactional
    public ConstructionStage createStage(UUID projectId, UUID customerId, UUID constructionRequestId,
                                         String name, String description, Instant startDate, Instant endDate) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        ConstructionRequest request = constructionRequestRepository.findById(constructionRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Construction request not found with id: " + constructionRequestId));

        ConstructionStage stage = new ConstructionStage();
        stage.setRequest(request);
        stage.setProject(project);
        stage.setCustomer(customer);
        stage.setName(name);
        stage.setDescription(description);
        stage.setStartDate(startDate);
        stage.setEndDate(endDate);
        stage.setStatus(StageStatus.PLANNED);

        return constructionStageRepository.save(stage);
    }


    @Transactional
    public ConstructionStage updateStage(UUID id, String name, String description,
                                         Instant startDate, Instant endDate, StageStatus status) {
        ConstructionStage stage = getStageById(id);

        if (name != null) {
            stage.setName(name);
        }
        if (description != null) {
            stage.setDescription(description);
        }
        if (startDate != null) {
            stage.setStartDate(startDate);
        }
        if (endDate != null) {
            stage.setEndDate(endDate);
        }
        if (status != null) {
            stage.setStatus(status);
        }

        return constructionStageRepository.save(stage);
    }

    @Transactional
    public ConstructionStage assignSpecialist(UUID stageId, UUID specialistId) {
        ConstructionStage stage = getStageById(stageId);

        User specialist = userRepository.findById(specialistId)
                .orElseThrow(() -> new EntityNotFoundException("Specialist not found with id: " + specialistId));

        stage.setSpecialist(specialist);
        return constructionStageRepository.save(stage);
    }

    @Transactional
    public ConstructionStage removeSpecialist(UUID stageId) {
        ConstructionStage stage = getStageById(stageId);
        stage.setSpecialist(null);
        return constructionStageRepository.save(stage);
    }

    @Transactional
    public ConstructionStage updateStatus(UUID stageId, StageStatus status) {
        ConstructionStage stage = getStageById(stageId);
        stage.setStatus(status);
        return constructionStageRepository.save(stage);
    }

    @Transactional
    public void deleteStage(UUID id) {
        if (!constructionStageRepository.existsById(id)) {
            throw new EntityNotFoundException("Construction stage not found with id: " + id);
        }
        constructionStageRepository.deleteById(id);
    }
}
