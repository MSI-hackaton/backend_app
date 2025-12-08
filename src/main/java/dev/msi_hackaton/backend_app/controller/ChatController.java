package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.request.ChatMessageCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import dev.msi_hackaton.backend_app.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{requestId}/messages")
    @Operation(summary = "Получить историю сообщений",
            description = "Возвращает все сообщения в чате по заявке")
    public List<ChatMessageResponseDto> getMessages(@PathVariable UUID requestId) {
        return chatService.getMessages(requestId);
    }

    @PostMapping("/{requestId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Отправить сообщение",
            description = "Отправляет новое сообщение в чат")
    public ChatMessageResponseDto sendMessage(
            @PathVariable UUID requestId,
            @RequestBody ChatMessageCreateDto messageDto) {
        return chatService.sendMessage(requestId, messageDto);
    }

    @PutMapping("/messages/{messageId}/read")
    @Operation(summary = "Отметить сообщение как прочитанное")
    public void markAsRead(@PathVariable UUID messageId) {
        chatService.markAsRead(messageId);
    }
}