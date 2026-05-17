package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;

public record StockMovementResponse(
        Long id,
        Long productId,
        String productCode,
        String productName,
        StockMovementType movementType,
        BigDecimal quantity,
        String referenceType,
        String referenceId,
        String performedBy,
        LocalDateTime occurredAt,
        String note
) {
}
