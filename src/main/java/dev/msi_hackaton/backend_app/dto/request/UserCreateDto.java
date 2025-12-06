package dev.msi_hackaton.backend_app.dto.request;

import dev.msi_hackaton.backend_app.dao.entities.enums.UserRole;
import lombok.Data;

@Data
public class UserCreateDto {
    private String email;
    private String phone;
    private String password;
    private String fullName;
    private UserRole role;
}
