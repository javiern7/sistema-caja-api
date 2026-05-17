package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.domain.ExpenseType;

public record ExpenseResponse(
        Long id,
        Long operationalContextId,
        String operationalContextName,
        Long cashBoxId,
        ExpenseType expenseType,
        String category,
        String description,
        String paymentMethod,
        BigDecimal amount,
        String responsible,
        String observation,
        String recordedByUsername,
        LocalDate expenseDate,
        LocalDateTime createdAt
) {
}
