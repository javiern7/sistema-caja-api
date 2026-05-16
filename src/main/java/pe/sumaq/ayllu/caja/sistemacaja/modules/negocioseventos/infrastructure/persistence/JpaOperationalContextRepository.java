package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;

public interface JpaOperationalContextRepository extends JpaRepository<OperationalContextEntity, Long> {

    List<OperationalContextEntity> findAllByStatusOrderByStartDateAsc(OperationalContextStatus status);
}
