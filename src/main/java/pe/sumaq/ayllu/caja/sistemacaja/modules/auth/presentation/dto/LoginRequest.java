package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example = "admin")
        @NotBlank(message = "El username es obligatorio.")
        String username,
        @Schema(example = "Admin123*")
        @NotBlank(message = "La password es obligatoria.")
        String password
) {
}
