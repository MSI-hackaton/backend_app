package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConstructionRequestCreateDto {
    private String fullName;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s-]{10,}$", message = "Phone number should be valid")
    private String phone;
}
