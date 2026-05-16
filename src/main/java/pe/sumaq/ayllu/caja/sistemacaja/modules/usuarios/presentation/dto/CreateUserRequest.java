package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "El username es obligatorio.")
        String username,
        @NotBlank(message = "La password es obligatoria.")
        String password,
        @NotNull(message = "El rol es obligatorio.")
        Long roleId,
        boolean active
) {
}
