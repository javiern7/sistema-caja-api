package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.time.LocalDateTime;

public record ReportHistoryResponse(
        Long id,
        String reportType,
        String format,
        String generatedBy,
        String filters,
        LocalDateTime generatedAt
) {
}
