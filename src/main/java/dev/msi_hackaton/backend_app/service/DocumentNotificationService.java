package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.Document;
import dev.msi_hackaton.backend_app.dao.entities.DocumentNotification;
import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import dev.msi_hackaton.backend_app.dao.repository.DocumentNotificationRepository;
import dev.msi_hackaton.backend_app.dao.repository.DocumentRepository;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.websocket.DocumentNotificationDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentNotificationService {

    private final DocumentNotificationRepository notificationRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public DocumentNotificationDto createStatusChangeNotification(
            UUID documentId,
            UUID changedByUserId,
            DocumentStatus oldStatus,
            DocumentStatus newStatus,
            String comment) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        User changedBy = userRepository.findById(changedByUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + changedByUserId));

        // Создаем уведомление для владельца (customer)
        User customer = document.getConstruction().getCustomer();

        DocumentNotification notification = new DocumentNotification();
        notification.setDocument(document);
        notification.setUser(customer);
        notification.setNotificationType("STATUS_CHANGE");
        notification.setMessage(String.format(
                "Статус документа '%s' изменен с '%s' на '%s' пользователем %s. Комментарий: %s",
                document.getName(),
                getStatusDisplayName(oldStatus),
                getStatusDisplayName(newStatus),
                changedBy.getFullName(),
                comment != null ? comment : "нет комментария"
        ));

        DocumentNotification saved = notificationRepository.save(notification);

        // Отправляем через WebSocket
        sendNotificationViaWebSocket(saved);

        return mapToDto(saved);
    }

    @Transactional
    public DocumentNotificationDto createNewDocumentNotification(
            UUID documentId,
            UUID uploadedByUserId) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        User uploadedBy = userRepository.findById(uploadedByUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + uploadedByUserId));

        // Создаем уведомление для специалиста
        User specialist = document.getConstruction().getSpecialist();
        if (specialist == null) {
            // Если нет специалиста, возвращаем null
            return null;
        }

        DocumentNotification notification = new DocumentNotification();
        notification.setDocument(document);
        notification.setUser(specialist);
        notification.setNotificationType("NEW_DOCUMENT");
        notification.setMessage(String.format(
                "Загружен новый документ '%s' для этапа '%s'",
                document.getName(),
                document.getConstruction().getName()
        ));

        DocumentNotification saved = notificationRepository.save(notification);

        // Отправляем через WebSocket
        sendNotificationViaWebSocket(saved);

        return mapToDto(saved);
    }

    @Transactional
    public DocumentNotificationDto createReminderNotification(
            UUID documentId,
            String reminderMessage) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        // Создаем уведомление для владельца
        User customer = document.getConstruction().getCustomer();

        DocumentNotification notification = new DocumentNotification();
        notification.setDocument(document);
        notification.setUser(customer);
        notification.setNotificationType("REMINDER");
        notification.setMessage(reminderMessage);

        DocumentNotification saved = notificationRepository.save(notification);

        // Отправляем через WebSocket
        sendNotificationViaWebSocket(saved);

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentNotificationDto> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DocumentNotificationDto> getAllNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        DocumentNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found: " + notificationId));

        // Проверяем, что уведомление принадлежит пользователю
        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Notification does not belong to user");
        }

        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        List<DocumentNotification> unreadNotifications =
                notificationRepository.findByUserIdAndIsReadFalse(userId);

        Instant now = Instant.now();
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    private void sendNotificationViaWebSocket(DocumentNotification notification) {
        DocumentNotificationDto dto = mapToDto(notification);

        // Отправляем персонально пользователю
        messagingTemplate.convertAndSendToUser(
                notification.getUser().getId().toString(),
                "/queue/documents/notifications",
                dto
        );

        // Также отправляем в общий канал для этапа строительства
        messagingTemplate.convertAndSend(
                "/topic/documents/" + notification.getDocument().getConstruction().getId() + "/notifications",
                dto
        );
    }

    private DocumentNotificationDto mapToDto(DocumentNotification notification) {
        DocumentNotificationDto dto = new DocumentNotificationDto();
        dto.setId(notification.getId());
        dto.setDocumentId(notification.getDocument().getId());
        dto.setUserId(notification.getUser().getId());
        dto.setNotificationType(notification.getNotificationType());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());

        // Дополнительные данные
        dto.setDocumentName(notification.getDocument().getName());
        dto.setConstructionName(notification.getDocument().getConstruction().getName());

        DocumentNotificationDto.DocumentStatusDto statusDto =
                new DocumentNotificationDto.DocumentStatusDto();
        statusDto.setStatus(notification.getDocument().getStatus().name());
        statusDto.setDisplayName(getStatusDisplayName(notification.getDocument().getStatus()));
        dto.setDocumentStatus(statusDto);

        return dto;
    }

    private String getStatusDisplayName(DocumentStatus status) {
        if (status == null) return "Неизвестно";
        return switch (status) {
            case UPLOADED -> "Загружено";
            case UNDER_REVIEW -> "На рассмотрении";
            case APPROVED -> "Одобрено";
            case REJECTED -> "Отклонено";
            default -> status.name();
        };
    }

    // Вспомогательный метод для получения UserId из Principal
    public UUID getUserIdFromPrincipal(Principal principal) {
        if (principal == null) return null;

        String username = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByPhone(username);
        }
        return userOptional.map(User::getId).orElse(null);
    }
}