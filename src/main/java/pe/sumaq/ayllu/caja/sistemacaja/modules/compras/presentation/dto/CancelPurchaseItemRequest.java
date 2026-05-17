package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CancelPurchaseItemRequest(
        @NotNull(message = "El detalle de compra es obligatorio.")
        Long purchaseItemId,
        @NotNull(message = "La cantidad anulada es obligatoria.")
        @DecimalMin(value = "0.01", message = "La cantidad anulada debe ser mayor a cero.")
        BigDecimal cancelledQuantity
) {
}
