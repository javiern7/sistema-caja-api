package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProviderRequest(
        @NotBlank(message = "El nombre del proveedor es obligatorio.")
        String name,
        String documentNumber,
        String contactName,
        String phone,
        String email,
        boolean active
) {
}
