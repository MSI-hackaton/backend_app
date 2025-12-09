package dev.msi_hackaton.backend_app.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class JwtAuthenticationResponse {

    private String token;
    private String type = "Bearer";
    private UUID id;
    private String email;
    private String phone;
    private List<String> roles;

    public JwtAuthenticationResponse(String token, UUID id, String email, String phone, List<String> roles) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.roles = roles;
    }
}
