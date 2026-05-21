package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;

public record SaleListResponse(
        Long id,
        Long operationalContextId,
        String operationalContextName,
        Long cashBoxId,
        String soldByUsername,
        SaleStatus status,
        BigDecimal subtotalAmount,
        BigDecimal totalAmount,
        String internalReceiptSeries,
        Long internalReceiptNumber,
        String observation,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt,
        String cancelledByUsername,
        String cancellationReason,
        int itemsCount,
        int paymentsCount
) {
}
