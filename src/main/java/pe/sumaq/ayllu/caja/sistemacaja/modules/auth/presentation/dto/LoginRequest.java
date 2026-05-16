package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El username es obligatorio.")
        String username,
        @NotBlank(message = "La password es obligatoria.")
        String password
) {
}
