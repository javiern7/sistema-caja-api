package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SalesReportResponse(
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        Long operationalContextId,
        int totalSales,
        BigDecimal totalAmount,
        List<SalesReportRowResponse> items
) {
}
