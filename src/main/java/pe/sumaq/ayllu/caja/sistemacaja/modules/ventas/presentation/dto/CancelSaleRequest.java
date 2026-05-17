package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelSaleRequest(
        @NotBlank(message = "El motivo de anulacion es obligatorio.")
        String reason
) {
}
