package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockCurrentRepository extends JpaRepository<StockCurrentEntity, StockCurrentId> {

    Optional<StockCurrentEntity> findByOperationalContextIdAndProductId(Long operationalContextId, Long productId);

    List<StockCurrentEntity> findAllByOperationalContextIdAndProductIdIn(Long operationalContextId, Collection<Long> productIds);

    List<StockCurrentEntity> findAllByOperationalContextId(Long operationalContextId);
}
