package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import java.math.BigDecimal;

public record SalePaymentResponse(
        Long id,
        String paymentMethod,
        BigDecimal amount
) {
}
