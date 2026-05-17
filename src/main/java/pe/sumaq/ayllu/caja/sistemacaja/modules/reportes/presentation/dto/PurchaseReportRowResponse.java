package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseReportRowResponse(
        Long purchaseId,
        LocalDate purchaseDate,
        Long operationalContextId,
        String operationalContextName,
        String providerName,
        String status,
        BigDecimal effectiveAmount
) {
}
