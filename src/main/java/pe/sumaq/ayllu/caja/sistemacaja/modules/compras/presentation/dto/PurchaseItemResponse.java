package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.math.BigDecimal;

public record PurchaseItemResponse(
        Long id,
        Long productId,
        String productCode,
        String productName,
        BigDecimal quantity,
        BigDecimal cancelledQuantity,
        BigDecimal unitCost,
        BigDecimal subtotalAmount
) {
}
