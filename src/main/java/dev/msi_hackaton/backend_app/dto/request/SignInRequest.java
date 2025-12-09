package dev.msi_hackaton.backend_app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {
    @Schema(description = "identifier", example = "jondoe@gmail.com")
    @NotBlank(message = "Поле не может быть пустыми")
    private String identifier;

    @Schema(description = "Пароль", example = "my_secret_password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;

}
