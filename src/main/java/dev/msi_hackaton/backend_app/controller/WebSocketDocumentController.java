package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import dev.msi_hackaton.backend_app.dao.repository.UserRepository;
import dev.msi_hackaton.backend_app.dto.websocket.DocumentNotificationDto;
import dev.msi_hackaton.backend_app.service.DocumentNotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebSocketDocumentController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DocumentNotificationService notificationService;
    private final UserRepository userRepository;

    @MessageMapping("/documents.notifyStatusChange")
    public void notifyDocumentStatusChange(
            @Payload DocumentStatusChangeMessage message,
            Principal principal) {

        // Отправляем уведомление всем подписанным пользователям
        String destination = "/topic/documents/" + message.getConstructionId() + "/status";

        messagingTemplate.convertAndSend(destination, message);

        // Также отправляем персональные уведомления
        UUID userId = getUserIdFromPrincipal(principal);
        if (userId != null) {
            notificationService.createStatusChangeNotification(
                    message.getDocumentId(),
                    userId,
                    message.getOldStatus(),
                    message.getNewStatus(),
                    message.getMessage()
            );
        }
    }

    @SubscribeMapping("/user/queue/documents/notifications")
    @SendToUser("/queue/documents/notifications")
    public List<DocumentNotificationDto> getUnreadNotifications(@AuthenticationPrincipal UUID userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    @MessageMapping("/documents.markNotificationAsRead")
    public void markNotificationAsRead(
            @Payload UUID notificationId,
            @AuthenticationPrincipal UUID userId) {
        notificationService.markAsRead(notificationId, userId);
    }

    private UUID getUserIdFromPrincipal(Principal principal) {
        if (principal == null) return null;

        String username = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByPhone(username);
        }
        return userOptional.map(User::getId).orElse(null);
    }

    @Data
    public static class DocumentStatusChangeMessage {
        private UUID documentId;
        private UUID constructionId;
        private String documentName;
        private DocumentStatus oldStatus;
        private DocumentStatus newStatus;
        private String changedBy;
        private String message;
    }
}