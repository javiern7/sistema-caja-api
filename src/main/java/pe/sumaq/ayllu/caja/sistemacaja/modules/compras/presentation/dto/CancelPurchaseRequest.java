package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CancelPurchaseRequest(
        @NotBlank(message = "El motivo de anulacion es obligatorio.")
        String reason,
        @NotEmpty(message = "Debe indicar al menos un item a anular.")
        List<@Valid CancelPurchaseItemRequest> cancelledItems
) {
}
