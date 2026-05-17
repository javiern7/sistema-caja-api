package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;

public record CashBoxDetailResponse(
        Long id,
        Long operationalContextId,
        String operationalContextCode,
        String operationalContextName,
        Long openedByUserId,
        String openedByUsername,
        CashBoxStatus status,
        BigDecimal openingAmount,
        BigDecimal totalSales,
        BigDecimal additionalIncome,
        BigDecimal totalExpenses,
        BigDecimal expectedAmount,
        BigDecimal countedAmount,
        BigDecimal differenceAmount,
        String openingObservation,
        String closingObservation,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        String closedByUsername,
        List<CashBoxMovementResponse> movements
) {
}
