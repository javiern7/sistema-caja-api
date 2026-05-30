package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockMovementEntity;

@Service
public class StockLedgerService {

    private final JpaStockCurrentRepository jpaStockCurrentRepository;
    private final JpaStockMovementRepository jpaStockMovementRepository;

    public StockLedgerService(
            JpaStockCurrentRepository jpaStockCurrentRepository,
            JpaStockMovementRepository jpaStockMovementRepository
    ) {
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
        this.jpaStockMovementRepository = jpaStockMovementRepository;
    }

    @Transactional
    public void increaseStock(
            OperationalContextEntity operationalContext,
            ProductEntity product,
            BigDecimal quantity,
            String performedBy,
            StockMovementType movementType,
            String referenceType,
            String referenceId,
            String note
    ) {
        StockCurrentEntity stockCurrent = findOrCreate(operationalContext, product);
        stockCurrent.setCurrentStock(stockCurrent.getCurrentStock().add(quantity));
        stockCurrent.setUpdatedAt(LocalDateTime.now());
        jpaStockCurrentRepository.save(stockCurrent);
        jpaStockMovementRepository.save(buildMovement(
                operationalContext,
                product,
                quantity,
                performedBy,
                movementType,
                referenceType,
                referenceId,
                note
        ));
    }

    @Transactional
    public void decreaseStock(
            OperationalContextEntity operationalContext,
            ProductEntity product,
            BigDecimal quantity,
            String performedBy,
            StockMovementType movementType,
            String referenceType,
            String referenceId,
            String note
    ) {
        StockCurrentEntity stockCurrent = jpaStockCurrentRepository
                .findByOperationalContextIdAndProductId(operationalContext.getId(), product.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.STOCK_INSUFICIENTE,
                        HttpStatus.CONFLICT,
                        "No existe stock disponible para el producto solicitado en el contexto operativo."
                ));

        if (stockCurrent.getCurrentStock().compareTo(quantity) < 0) {
            throw new BusinessException(
                    ErrorCode.STOCK_INSUFICIENTE,
                    HttpStatus.CONFLICT,
                    "El producto no cuenta con stock suficiente en el contexto operativo."
            );
        }

        stockCurrent.setCurrentStock(stockCurrent.getCurrentStock().subtract(quantity));
        stockCurrent.setUpdatedAt(LocalDateTime.now());
        jpaStockCurrentRepository.save(stockCurrent);
        jpaStockMovementRepository.save(buildMovement(
                operationalContext,
                product,
                quantity,
                performedBy,
                movementType,
                referenceType,
                referenceId,
                note
        ));
    }

    private StockCurrentEntity findOrCreate(OperationalContextEntity operationalContext, ProductEntity product) {
        return jpaStockCurrentRepository.findByOperationalContextIdAndProductId(operationalContext.getId(), product.getId())
                .orElseGet(() -> {
                    StockCurrentEntity newStock = new StockCurrentEntity();
                    newStock.setOperationalContextId(operationalContext.getId());
                    newStock.setOperationalContext(operationalContext);
                    newStock.setProductId(product.getId());
                    newStock.setProduct(product);
                    newStock.setCurrentStock(BigDecimal.ZERO);
                    newStock.setUpdatedAt(LocalDateTime.now());
                    return newStock;
                });
    }

    private StockMovementEntity buildMovement(
            OperationalContextEntity operationalContext,
            ProductEntity product,
            BigDecimal quantity,
            String performedBy,
            StockMovementType movementType,
            String referenceType,
            String referenceId,
            String note
    ) {
        StockMovementEntity movement = new StockMovementEntity();
        movement.setOperationalContext(operationalContext);
        movement.setProduct(product);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);
        movement.setPerformedBy(performedBy);
        movement.setOccurredAt(LocalDateTime.now());
        movement.setNote(note);
        return movement;
    }
}
