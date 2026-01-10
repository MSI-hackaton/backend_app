package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.UserDto;
import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import dev.msi_hackaton.backend_app.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/constructions/{constructionId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponseDto sendMessage(
            @PathVariable UUID constructionId,
            @AuthenticationPrincipal UserDto user,
            @Valid @RequestBody ChatMessageCreateDto createDto) {
        return chatService.sendMessage(constructionId, user.getId(), createDto);
    }

    @GetMapping("/constructions/{constructionId}/messages")
    @Operation(summary = "Получить историю чата",
            description = "Возвращает все сообщения в чате строительной площадки")
    public List<ChatMessageResponseDto> getChatHistory(@PathVariable UUID constructionId) {
        return chatService.getChatHistory(constructionId);
    }

    @PatchMapping("/messages/{messageId}/read")
    @Operation(summary = "Отметить сообщение как прочитанное",
            description = "Отмечает сообщение как прочитанное текущим пользователем")
    public void markAsRead(@PathVariable UUID messageId) {
        chatService.markAsRead(messageId);
    }

    @GetMapping("/constructions/{constructionId}/unread-count")
    @Operation(summary = "Получить количество непрочитанных сообщений",
            description = "Возвращает количество непрочитанных сообщений в чате")
    public Long getUnreadCount(
            @PathVariable UUID constructionId,
            @AuthenticationPrincipal UUID userId) {
        return chatService.getUnreadCount(constructionId, userId);
    }
}