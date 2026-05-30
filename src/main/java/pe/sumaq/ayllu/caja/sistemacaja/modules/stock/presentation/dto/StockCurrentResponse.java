package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockCurrentResponse(
        Long operationalContextId,
        Long productId,
        String productCode,
        String productName,
        String unitOfMeasure,
        boolean stockControlled,
        boolean productActive,
        BigDecimal currentStock,
        BigDecimal minimumStock,
        LocalDateTime updatedAt
) {
}
