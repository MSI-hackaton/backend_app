package dev.msi_hackaton.backend_app.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    // private String type = "Bearer";
    private UUID id;
    private String email;
    private String phone;
    // private List<String> roles;
}
