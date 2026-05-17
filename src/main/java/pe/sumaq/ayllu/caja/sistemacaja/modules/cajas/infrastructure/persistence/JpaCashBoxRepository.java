package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;

public interface JpaCashBoxRepository extends JpaRepository<CashBoxEntity, Long> {

    boolean existsByOpenedByIdAndStatus(Long userId, CashBoxStatus status);

    boolean existsByOperationalContextIdAndStatus(Long operationalContextId, CashBoxStatus status);

    List<CashBoxEntity> findAllByOrderByOpenedAtDesc();

    Optional<CashBoxEntity> findFirstByOpenedByIdAndStatusOrderByOpenedAtDesc(Long userId, CashBoxStatus status);
}
