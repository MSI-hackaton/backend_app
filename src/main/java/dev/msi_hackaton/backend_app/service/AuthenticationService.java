package dev.msi_hackaton.backend_app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.msi_hackaton.backend_app.dto.UserDto;
import dev.msi_hackaton.backend_app.dto.response.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;

    public JwtAuthenticationResponse getJwtAuthenticationResponse(String identifier, String code){
        UserDto user;
        if (identifier.contains("@")) {
            user = userService.getUserByEmail(identifier);
        } else {
            user = userService.getUserByEmail(identifier);
        }

        if (!validateCode(code, identifier)) {
            throw new RuntimeException("Invalid code");
        }

        String jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt,
                user.getId(), user.getEmail(), user.getPhone());

    }

    public UUID getUserIdFromToken(String token) {
        return jwtService.getUserIdFromToken(token);
    }

    private boolean validateCode(String identifier, String code) {
        return true;
    }
}
