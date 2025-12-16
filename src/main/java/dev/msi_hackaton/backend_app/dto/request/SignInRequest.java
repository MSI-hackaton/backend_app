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
    @Schema(description = "Идентификатор: телефон или email", example = "jondoe@gmail.com")
    @NotBlank(message = "Поле не может быть пустыми")
    private String identifier;

    @Schema(description = "Проверочный код", example = "1111")
    @Size(min = 4, max = 6, message = "Длина проверочного кода должна быть от 4 до 6 символов")
    @NotBlank(message = "Поле код обязательно для заполнения.")
    private String code;
}