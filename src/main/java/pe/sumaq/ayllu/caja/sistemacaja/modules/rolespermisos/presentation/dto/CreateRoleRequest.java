package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
        @NotBlank(message = "El nombre del rol es obligatorio.")
        String name,
        String description,
        List<String> permissions
) {
}
