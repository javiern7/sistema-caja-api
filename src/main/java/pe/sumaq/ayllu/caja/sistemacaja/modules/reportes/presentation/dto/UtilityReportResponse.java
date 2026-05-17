package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UtilityReportResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        Long operationalContextId,
        BigDecimal salesAmount,
        BigDecimal purchaseAmount,
        BigDecimal expenseAmount,
        BigDecimal estimatedCostOfSales,
        BigDecimal grossMargin,
        BigDecimal netUtility
) {
}
