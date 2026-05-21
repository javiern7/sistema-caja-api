package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxListResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxMovementResponse;

@Component
public class CashBoxMapper {

    public CashBoxListResponse toListResponse(CashBoxEntity cashBoxEntity) {
        return new CashBoxListResponse(
                cashBoxEntity.getId(),
                cashBoxEntity.getOperationalContext().getId(),
                cashBoxEntity.getOperationalContext().getCode(),
                cashBoxEntity.getOperationalContext().getName(),
                cashBoxEntity.getOpenedBy().getId(),
                cashBoxEntity.getOpenedBy().getUsername(),
                cashBoxEntity.getStatus(),
                cashBoxEntity.getOpeningAmount(),
                cashBoxEntity.getExpectedAmount(),
                cashBoxEntity.getCountedAmount(),
                cashBoxEntity.getDifferenceAmount(),
                cashBoxEntity.getOpeningObservation(),
                cashBoxEntity.getClosingObservation(),
                cashBoxEntity.getOpenedAt(),
                cashBoxEntity.getClosedAt(),
                cashBoxEntity.getClosedBy() != null ? cashBoxEntity.getClosedBy().getUsername() : null
        );
    }

    public CashBoxDetailResponse toDetailResponse(
            CashBoxEntity cashBoxEntity,
            List<CashMovementEntity> movementEntities
    ) {
        BigDecimal totalSales = sumByType(movementEntities, CashMovementType.VENTA, CashMovementType.ANULACION_VENTA);
        BigDecimal additionalIncome = sumByType(movementEntities, CashMovementType.INGRESO_AJUSTE);
        BigDecimal totalExpenses = sumByType(movementEntities, CashMovementType.EGRESO);
        BigDecimal computedExpected = cashBoxEntity.getOpeningAmount()
                .add(totalSales)
                .add(additionalIncome)
                .subtract(totalExpenses);
        BigDecimal expectedAmount = cashBoxEntity.getExpectedAmount() != null
                ? cashBoxEntity.getExpectedAmount()
                : computedExpected;

        return new CashBoxDetailResponse(
                cashBoxEntity.getId(),
                cashBoxEntity.getOperationalContext().getId(),
                cashBoxEntity.getOperationalContext().getCode(),
                cashBoxEntity.getOperationalContext().getName(),
                cashBoxEntity.getOpenedBy().getId(),
                cashBoxEntity.getOpenedBy().getUsername(),
                cashBoxEntity.getStatus(),
                cashBoxEntity.getOpeningAmount(),
                totalSales,
                additionalIncome,
                totalExpenses,
                expectedAmount,
                cashBoxEntity.getCountedAmount(),
                cashBoxEntity.getDifferenceAmount(),
                cashBoxEntity.getOpeningObservation(),
                cashBoxEntity.getClosingObservation(),
                cashBoxEntity.getOpenedAt(),
                cashBoxEntity.getClosedAt(),
                cashBoxEntity.getClosedBy() != null ? cashBoxEntity.getClosedBy().getUsername() : null,
                movementEntities.stream().map(this::toMovementResponse).toList()
        );
    }

    public CashBoxMovementResponse toMovementResponse(CashMovementEntity movementEntity) {
        return new CashBoxMovementResponse(
                movementEntity.getId(),
                movementEntity.getMovementType(),
                movementEntity.getAmount(),
                movementEntity.getReferenceType(),
                movementEntity.getReferenceId(),
                movementEntity.getPerformedBy(),
                movementEntity.getOccurredAt(),
                movementEntity.getObservation()
        );
    }

    private BigDecimal sumByType(List<CashMovementEntity> movements, CashMovementType... movementTypes) {
        List<CashMovementType> typeList = List.of(movementTypes);

        return movements.stream()
                .filter(movement -> typeList.contains(movement.getMovementType()))
                .map(CashMovementEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
