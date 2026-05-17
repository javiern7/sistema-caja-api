package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PurchaseItemRequest(
        @NotNull(message = "El producto es obligatorio.")
        Long productId,
        @NotNull(message = "La cantidad es obligatoria.")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero.")
        BigDecimal quantity,
        @NotNull(message = "El costo unitario es obligatorio.")
        @DecimalMin(value = "0.00", message = "El costo unitario no puede ser negativo.")
        BigDecimal unitCost
) {
}
