package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StockReportRowResponse(
        Long productId,
        String productCode,
        String productName,
        String unitOfMeasure,
        boolean active,
        boolean stockControlled,
        BigDecimal minimumStock,
        BigDecimal currentStock,
        LocalDateTime updatedAt
) {
}
