package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.util.List;

public record StockReportResponse(
        String stockScope,
        int totalProducts,
        BigDecimal totalUnits,
        List<StockReportRowResponse> items
) {
}
