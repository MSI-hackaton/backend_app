package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dto.response.NotificationResponse;
import dev.msi_hackaton.backend_app.entity.Notification;
import dev.msi_hackaton.backend_app.entity.User;
import dev.msi_hackaton.backend_app.repository.NotificationRepository;
import dev.msi_hackaton.backend_app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NotificationResponse createNotification(Long userId, String title, String message,
                                                   String type, Long relatedEntityId, String relatedEntityType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId, Boolean unreadOnly) {
        List<Notification> notifications;
        if (unreadOnly != null && unreadOnly) {

            notifications = notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId);
        } else {

            notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        }

        return notifications.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Уведомление не найдено"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {

        List<Notification> unreadNotifications =
                notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {

        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUser().getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setIsRead(notification.getIsRead());
        response.setRelatedEntityId(notification.getRelatedEntityId());
        response.setRelatedEntityType(notification.getRelatedEntityType());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}