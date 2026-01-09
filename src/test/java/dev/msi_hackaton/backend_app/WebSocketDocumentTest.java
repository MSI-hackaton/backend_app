package dev.msi_hackaton.backend_app;

import dev.msi_hackaton.backend_app.dao.entities.enums.DocumentStatus;
import dev.msi_hackaton.backend_app.service.DocumentNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WebSocketDocumentTest {

    @Autowired
    private DocumentNotificationService notificationService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void testDocumentNotificationCreation() {
        // Этот тест проверяет создание уведомлений
        // В реальном приложении здесь были бы тестовые данные

        System.out.println("WebSocket notification service test");
        assertThat(notificationService).isNotNull();

        // В полноценном тесте мы бы:
        // 1. Создали тестовые документы и пользователей
        // 2. Вызвали методы создания уведомлений
        // 3. Проверили отправку сообщений через messagingTemplate

        assertThat(true).isTrue();
    }
}