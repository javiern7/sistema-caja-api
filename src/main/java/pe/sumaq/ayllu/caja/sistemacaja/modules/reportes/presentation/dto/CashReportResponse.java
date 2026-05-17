package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CashReportResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        Long operationalContextId,
        int totalCashBoxes,
        BigDecimal totalOpeningAmount,
        BigDecimal totalExpectedAmount,
        BigDecimal totalDifferenceAmount,
        List<CashReportRowResponse> items
) {
}
