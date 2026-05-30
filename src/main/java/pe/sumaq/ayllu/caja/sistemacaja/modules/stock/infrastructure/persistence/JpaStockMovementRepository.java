package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockMovementRepository extends JpaRepository<StockMovementEntity, Long> {

    List<StockMovementEntity> findAllByOperationalContextIdOrderByOccurredAtDesc(Long operationalContextId);

    Page<StockMovementEntity> findAllByOperationalContextIdOrderByOccurredAtDesc(Long operationalContextId, Pageable pageable);
}
