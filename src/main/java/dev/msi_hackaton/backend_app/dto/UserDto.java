package dev.msi_hackaton.backend_app.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email; 
    private String phone;
    private String fullName;
}
