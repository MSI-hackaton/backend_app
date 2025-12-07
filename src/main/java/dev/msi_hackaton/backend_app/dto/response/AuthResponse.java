package dev.msi_hackaton.backend_app.dto.response;

public class AuthResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;

    public AuthResponse() {}

    public AuthResponse(String token, Long userId, String fullName, String email) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}