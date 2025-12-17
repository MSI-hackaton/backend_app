package dev.msi_hackaton.backend_app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос проверочного кода для авторизации.")
public class CodeRequest {
    @Schema(description = "Идентификатор: телефон или email", example = "jondoe@gmail.com")
    @NotBlank(message = "Поле не может быть пустыми")
    private String identifier;
}
