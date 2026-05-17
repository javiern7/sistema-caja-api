package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseReportRowResponse(
        Long expenseId,
        LocalDate expenseDate,
        Long operationalContextId,
        String operationalContextName,
        String expenseType,
        String category,
        String description,
        BigDecimal amount,
        String recordedByUsername
) {
}
