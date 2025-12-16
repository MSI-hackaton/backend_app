package dev.msi_hackaton.backend_app.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.msi_hackaton.backend_app.dto.UserDto;
import dev.msi_hackaton.backend_app.dto.request.CodeRequest;
import dev.msi_hackaton.backend_app.dto.request.SignInRequest;
import dev.msi_hackaton.backend_app.service.AuthenticationService;
import dev.msi_hackaton.backend_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;
    private final UserService userService;
    
    @Operation(summary = "Запрос проверочного кода.")
    @PostMapping("/code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody CodeRequest request) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Авторизация пользователя по email-коду")
    @PostMapping("/sign-in")
    public ResponseEntity<?> authUser(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(
            authService.getJwtAuthenticationResponse(request.getIdentifier(), request.getCode())
        );
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth(@RequestHeader("Authorization") String authHeader) {
        try {
            // Получаем токен из заголовка "Bearer <token>"
            String token = authHeader.replace("Bearer ", "").trim();

            // Извлекаем UUID пользователя из токена
            UUID userId = authService.getUserIdFromToken(token);

            // Получаем пользователя по ID
            UserDto user = userService.getUserById(userId);

            return ResponseEntity.ok(user); // возвращаем объект пользователя
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
        }
    }
}
