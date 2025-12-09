package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.websocket.WebSocketChatMessageDto;
import dev.msi_hackaton.backend_app.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Controller
public class WebSocketChatController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketChatController.class);

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Обработка входящих сообщений чата
     * Клиент отправляет на: /app/chat.send
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public WebSocketChatMessageDto sendMessage(
            @Payload WebSocketChatMessageDto chatMessage,
            Principal principal) {

        log.info("Получено WebSocket сообщение от {}: {}",
                principal != null ? principal.getName() : "unknown",
                chatMessage.getMessage());

        // Устанавливаем отправителя и время
        if (principal != null) {
            chatMessage.setSenderName(principal.getName());
        }
        chatMessage.setTimestamp(Instant.now());
        chatMessage.setType("CHAT");

        return chatMessage;
    }

    /**
     * Обработка сообщений для конкретной заявки
     */
    @MessageMapping("/chat/{requestId}/send")
    @SendTo("/topic/chat/{requestId}")
    public WebSocketChatMessageDto sendPrivateMessage(
            @DestinationVariable String requestId,
            @Payload ChatMessageCreateDto messageDto,
            Principal principal) {

        log.info("Сообщение для заявки {} от {}: {}",
                requestId,
                principal != null ? principal.getName() : "unknown",
                messageDto.getMessage());

        try {
            // Конвертируем в WebSocket DTO
            WebSocketChatMessageDto wsDto = new WebSocketChatMessageDto();
            wsDto.setType("MESSAGE");
            wsDto.setRequestId(UUID.fromString(requestId));
            wsDto.setSenderName(principal != null ? principal.getName() : "Аноним");
            wsDto.setMessage(messageDto.getMessage());
            wsDto.setTimestamp(Instant.now());
            wsDto.setIsRead(false);

            // Сохраняем в базу через сервис
            if (principal != null) {
                // TODO: Здесь нужно получить ID пользователя из principal
                // Пока используем эмуляцию
                chatService.sendMessage(UUID.fromString(requestId), messageDto);
            }

            log.info("Сообщение обработано для заявки {}", requestId);
            return wsDto;

        } catch (Exception e) {
            log.error("Ошибка обработки сообщения: {}", e.getMessage(), e);

            WebSocketChatMessageDto errorDto = new WebSocketChatMessageDto();
            errorDto.setType("ERROR");
            errorDto.setMessage("Ошибка: " + e.getMessage());
            errorDto.setTimestamp(Instant.now());

            return errorDto;
        }
    }

    /**
     * Добавление пользователя в чат
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public WebSocketChatMessageDto addUser(
            @Payload WebSocketChatMessageDto chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {

        // Добавляем username в WebSocket session
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        }

        chatMessage.setType("JOIN");
        chatMessage.setTimestamp(Instant.now());

        log.info("Пользователь {} присоединился к чату", chatMessage.getSenderName());
        return chatMessage;
    }

    /**
     * Присоединение к чату конкретной заявки
     */
    @MessageMapping("/chat/{requestId}/join")
    @SendTo("/topic/chat/{requestId}")
    public Map<String, Object> joinChat(
            @DestinationVariable String requestId,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal) {

        String username = principal != null ? principal.getName() : "Аноним";

        // Сохраняем в атрибутах сессии
        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", username);
            headerAccessor.getSessionAttributes().put("requestId", requestId);
        }

        log.info("{} присоединился к чату заявки {}", username, requestId);

        return Map.of(
                "type", "JOIN",
                "username", username,
                "requestId", requestId,
                "timestamp", Instant.now().toString()
        );
    }

    /**
     * Типинг индикатор
     */
    @MessageMapping("/chat/{requestId}/typing")
    @SendTo("/topic/chat/{requestId}")
    public Map<String, Object> typing(
            @DestinationVariable String requestId,
            Principal principal) {

        String username = principal != null ? principal.getName() : "Аноним";

        return Map.of(
                "type", "TYPING",
                "username", username,
                "requestId", requestId,
                "timestamp", Instant.now().toString()
        );
    }
}