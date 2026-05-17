package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OpenCashBoxRequest(
        @NotNull(message = "El contexto operativo es obligatorio.")
        Long operationalContextId,
        @NotNull(message = "El monto inicial es obligatorio.")
        @DecimalMin(value = "0.00", message = "El monto inicial no puede ser negativo.")
        BigDecimal openingAmount,
        String observation
) {
}
