package dev.msi_hackaton.backend_app.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "StroyControl API"); // Английский текст
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        response.put("message", "Server is running");
        response.put("database", "PostgreSQL on port 5433");
        return response;
    }

    @GetMapping("/test")
    public String test() {
        return "StroyControl API is working! Time: " + LocalDateTime.now();
    }
}