package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStockCurrentRepository extends JpaRepository<StockCurrentEntity, Long> {
}
