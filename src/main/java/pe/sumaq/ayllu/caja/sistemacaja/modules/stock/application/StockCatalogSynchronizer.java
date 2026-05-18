package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;

@Component
public class StockCatalogSynchronizer {

    private final JpaStockCurrentRepository jpaStockCurrentRepository;
    private final JpaProductRepository jpaProductRepository;

    public StockCatalogSynchronizer(
            JpaStockCurrentRepository jpaStockCurrentRepository,
            JpaProductRepository jpaProductRepository
    ) {
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
        this.jpaProductRepository = jpaProductRepository;
    }

    public void ensureCurrentStockRow(ProductEntity productEntity) {
        if (!productEntity.isStockControlled() || productEntity.getId() == null) {
            return;
        }

        jpaStockCurrentRepository.findById(productEntity.getId())
                .orElseGet(() -> {
                    ProductEntity managedProduct = jpaProductRepository.getReferenceById(productEntity.getId());
                    StockCurrentEntity stockCurrentEntity = new StockCurrentEntity();
                    stockCurrentEntity.setProductId(productEntity.getId());
                    stockCurrentEntity.setProduct(managedProduct);
                    stockCurrentEntity.setCurrentStock(BigDecimal.ZERO);
                    stockCurrentEntity.setUpdatedAt(LocalDateTime.now());
                    return jpaStockCurrentRepository.save(stockCurrentEntity);
                });
    }
}
