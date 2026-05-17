package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashMovementType;

public record CashBoxMovementResponse(
        Long id,
        CashMovementType movementType,
        BigDecimal amount,
        String referenceType,
        String referenceId,
        String performedBy,
        LocalDateTime occurredAt,
        String observation
) {
}
