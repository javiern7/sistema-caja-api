package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import java.math.BigDecimal;

public record SaleItemResponse(
        Long id,
        Long productId,
        String productCode,
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal subtotalAmount
) {
}
