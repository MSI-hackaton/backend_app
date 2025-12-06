package dev.msi_hackaton.backend_app.dto.nested;

import lombok.Data;

import java.util.UUID;

@Data
public class UserNestedDto {
    private UUID id;
    private String fullName;
    private String email;
}
