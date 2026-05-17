package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;

public record CashReportRowResponse(
        Long cashBoxId,
        Long operationalContextId,
        String operationalContextName,
        String openedByUsername,
        String closedByUsername,
        CashBoxStatus status,
        BigDecimal openingAmount,
        BigDecimal expectedAmount,
        BigDecimal countedAmount,
        BigDecimal differenceAmount,
        LocalDateTime openedAt,
        LocalDateTime closedAt
) {
}
