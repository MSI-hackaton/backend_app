package dev.msi_hackaton.backend_app.controller;

import dev.msi_hackaton.backend_app.dto.websocket.WebSocketMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class WebSocketChatController {

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public WebSocketMessageDto sendMessage(
            @Payload WebSocketMessageDto message,
            Principal principal) {

        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        return message;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public WebSocketMessageDto addUser(
            @Payload WebSocketMessageDto message,
            SimpMessageHeaderAccessor headerAccessor) {

        headerAccessor.getSessionAttributes().put("username", message.getSenderId());
        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        return message;
    }

    @MessageMapping("/chat.private")
    @SendToUser("/queue/private")
    public WebSocketMessageDto sendPrivateMessage(
            @Payload WebSocketMessageDto message,
            Principal principal) {

        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
        return message;
    }
}