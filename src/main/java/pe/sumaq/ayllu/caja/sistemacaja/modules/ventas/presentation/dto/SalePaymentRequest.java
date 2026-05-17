package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SalePaymentRequest(
        @NotBlank(message = "El metodo de pago es obligatorio.")
        String paymentMethod,
        @NotNull(message = "El monto es obligatorio.")
        @DecimalMin(value = "0.00", message = "El monto no puede ser negativo.")
        BigDecimal amount
) {
}
