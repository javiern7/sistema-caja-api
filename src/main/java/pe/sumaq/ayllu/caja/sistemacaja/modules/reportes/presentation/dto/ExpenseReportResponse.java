package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseReportResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        Long operationalContextId,
        int totalExpenses,
        BigDecimal totalAmount,
        List<ExpenseReportRowResponse> items
) {
}
