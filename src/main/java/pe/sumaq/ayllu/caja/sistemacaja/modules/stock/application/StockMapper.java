package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockCurrentResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockMovementResponse;

@Component
public class StockMapper {

    public StockCurrentResponse toCurrentResponse(ProductEntity productEntity, StockCurrentEntity stockCurrentEntity) {
        return new StockCurrentResponse(
                stockCurrentEntity == null ? null : stockCurrentEntity.getOperationalContextId(),
                productEntity.getId(),
                productEntity.getCode(),
                productEntity.getName(),
                productEntity.getUnitOfMeasure(),
                productEntity.isStockControlled(),
                productEntity.isActive(),
                stockCurrentEntity == null ? BigDecimal.ZERO : stockCurrentEntity.getCurrentStock(),
                productEntity.getMinimumStock(),
                stockCurrentEntity == null ? null : stockCurrentEntity.getUpdatedAt()
        );
    }

    public StockMovementResponse toMovementResponse(StockMovementEntity movementEntity) {
        return new StockMovementResponse(
                movementEntity.getId(),
                movementEntity.getOperationalContext().getId(),
                movementEntity.getOperationalContext().getName(),
                movementEntity.getProduct().getId(),
                movementEntity.getProduct().getCode(),
                movementEntity.getProduct().getName(),
                movementEntity.getMovementType(),
                movementEntity.getQuantity(),
                movementEntity.getReferenceType(),
                movementEntity.getReferenceId(),
                movementEntity.getPerformedBy(),
                movementEntity.getOccurredAt(),
                movementEntity.getNote()
        );
    }
}
