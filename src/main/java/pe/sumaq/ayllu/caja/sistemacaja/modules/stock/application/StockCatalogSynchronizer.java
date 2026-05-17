package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;

@Component
public class StockCatalogSynchronizer {

    private final JpaStockCurrentRepository jpaStockCurrentRepository;

    public StockCatalogSynchronizer(JpaStockCurrentRepository jpaStockCurrentRepository) {
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
    }

    public void ensureCurrentStockRow(ProductEntity productEntity) {
        if (!productEntity.isStockControlled()) {
            return;
        }

        jpaStockCurrentRepository.findById(productEntity.getId())
                .orElseGet(() -> {
                    StockCurrentEntity stockCurrentEntity = new StockCurrentEntity();
                    stockCurrentEntity.setProductId(productEntity.getId());
                    stockCurrentEntity.setProduct(productEntity);
                    stockCurrentEntity.setCurrentStock(BigDecimal.ZERO);
                    stockCurrentEntity.setUpdatedAt(LocalDateTime.now());
                    return jpaStockCurrentRepository.save(stockCurrentEntity);
                });
    }
}
