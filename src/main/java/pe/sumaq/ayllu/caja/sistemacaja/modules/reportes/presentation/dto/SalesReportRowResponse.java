package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesReportRowResponse(
        Long saleId,
        LocalDateTime createdAt,
        Long operationalContextId,
        String operationalContextName,
        String soldByUsername,
        String internalReceipt,
        BigDecimal totalAmount,
        int itemsCount
) {
}
