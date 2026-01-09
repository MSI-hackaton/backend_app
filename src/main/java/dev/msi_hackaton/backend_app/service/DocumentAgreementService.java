package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.*;
import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentAgreementStatus;
import dev.msi_hackaton.backend_app.dao.repository.*;
import dev.msi_hackaton.backend_app.dto.request.DocumentAgreementCreateDto;
import dev.msi_hackaton.backend_app.dto.response.DocumentAgreementResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentAgreementService {

    private final DocumentAgreementRepository documentAgreementRepository;
    private final ConstructionRequestRepository constructionRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public DocumentAgreementResponseDto createDocumentAgreement(
            UUID requestId, DocumentAgreementCreateDto createDto) {

        ConstructionRequest request = constructionRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Construction request not found: " + requestId));

        DocumentAgreement agreement = new DocumentAgreement();
        agreement.setConstructionRequest(request);
        agreement.setTitle(createDto.getTitle());
        agreement.setDescription(createDto.getDescription());
        agreement.setStatus(DocumentAgreementStatus.PENDING_REVIEW);
        agreement.setRequiredSignatures(createDto.getRequiredSignatures() != null ?
                createDto.getRequiredSignatures() : true);

        if (createDto.getDeadline() != null) {
            agreement.setDeadline(Instant.parse(createDto.getDeadline()));
        }

        DocumentAgreement saved = documentAgreementRepository.save(agreement);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentAgreementResponseDto> getAgreementsByRequest(UUID requestId) {
        return documentAgreementRepository.findByConstructionRequestIdOrderByCreatedAtDesc(requestId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public DocumentAgreementResponseDto updateStatus(UUID agreementId, String status, String comment) {
        DocumentAgreement agreement = documentAgreementRepository.findById(agreementId)
                .orElseThrow(() -> new EntityNotFoundException("Document agreement not found: " + agreementId));

        try {
            DocumentAgreementStatus newStatus = DocumentAgreementStatus.valueOf(status.toUpperCase());
            agreement.setStatus(newStatus);

            if (newStatus == DocumentAgreementStatus.SIGNED) {
                agreement.setSignedAt(Instant.now());
            }

            DocumentAgreement updated = documentAgreementRepository.save(agreement);
            return mapToDto(updated);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Transactional
    public DocumentAgreementResponseDto signDocument(UUID agreementId, UUID userId, String signatureData) {
        DocumentAgreement agreement = documentAgreementRepository.findById(agreementId)
                .orElseThrow(() -> new EntityNotFoundException("Document agreement not found: " + agreementId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        agreement.setStatus(DocumentAgreementStatus.SIGNED);
        agreement.setSignedAt(Instant.now());
        agreement.setSignedBy(user);
        agreement.setSignatureData(signatureData);

        DocumentAgreement updated = documentAgreementRepository.save(agreement);
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public DocumentAgreementResponseDto getAgreementById(UUID agreementId) {
        DocumentAgreement agreement = documentAgreementRepository.findById(agreementId)
                .orElseThrow(() -> new EntityNotFoundException("Document agreement not found: " + agreementId));
        return mapToDto(agreement);
    }

    @Transactional(readOnly = true)
    public List<DocumentAgreementResponseDto> getPendingAgreementsForUser(UUID userId) {

        return documentAgreementRepository.findAll().stream()
                .filter(agreement -> agreement.getStatus() == DocumentAgreementStatus.CLIENT_REVIEW)
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentAgreementResponseDto> getDocumentChecklist(UUID requestId) {
        return getAgreementsByRequest(requestId).stream()
                .filter(agreement -> agreement.getRequiredSignatures() != null && agreement.getRequiredSignatures())
                .toList();
    }

    private DocumentAgreementResponseDto mapToDto(DocumentAgreement agreement) {
        DocumentAgreementResponseDto dto = new DocumentAgreementResponseDto();
        dto.setId(agreement.getId());
        dto.setRequestId(agreement.getConstructionRequest().getId());
        dto.setTitle(agreement.getTitle());
        dto.setDescription(agreement.getDescription());
        dto.setStatus(agreement.getStatus());
        dto.setRequiredSignatures(agreement.getRequiredSignatures());
        dto.setDeadline(agreement.getDeadline());
        dto.setSignedAt(agreement.getSignedAt());
        dto.setCreatedAt(agreement.getCreatedAt());

        if (agreement.getSignedBy() != null) {
            dto.setSignedById(agreement.getSignedBy().getId());
            dto.setSignedByName(agreement.getSignedBy().getFullName());
        }

        return dto;
    }
}