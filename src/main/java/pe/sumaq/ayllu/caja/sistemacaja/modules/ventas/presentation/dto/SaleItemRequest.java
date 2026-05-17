package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record SaleItemRequest(
        @NotNull(message = "El producto es obligatorio.")
        Long productId,
        @NotNull(message = "La cantidad es obligatoria.")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero.")
        BigDecimal quantity,
        @NotNull(message = "El precio unitario es obligatorio.")
        @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo.")
        BigDecimal unitPrice
) {
}
