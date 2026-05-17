package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CloseCashBoxRequest(
        @NotNull(message = "El monto contado es obligatorio.")
        @DecimalMin(value = "0.00", message = "El monto contado no puede ser negativo.")
        BigDecimal countedAmount,
        String observation
) {
}
