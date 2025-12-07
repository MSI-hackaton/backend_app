package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ChatMessageRequest;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponse;
import dev.msi_hackaton.backend_app.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Чат", description = "API для общения с менеджером")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    @Operation(summary = "Отправить сообщение")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            @RequestParam Long senderId) {
        ChatMessageResponse message = chatService.sendMessage(request, senderId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Получить историю сообщений")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long orderId) {
        List<ChatMessageResponse> messages = chatService.getOrderMessages(orderId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/order/{orderId}/read")
    @Operation(summary = "Пометить сообщения как прочитанные")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        chatService.markMessagesAsRead(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}/unread-count")
    @Operation(summary = "Получить количество непрочитанных сообщений")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long orderId,
            @RequestParam Long userId) {
        Long count = chatService.getUnreadCount(orderId, userId);
        return ResponseEntity.ok(count);
    }
}