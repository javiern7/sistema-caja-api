package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCashMovementRepository extends JpaRepository<CashMovementEntity, Long> {

    List<CashMovementEntity> findAllByCashBoxIdOrderByOccurredAtAsc(Long cashBoxId);
}
