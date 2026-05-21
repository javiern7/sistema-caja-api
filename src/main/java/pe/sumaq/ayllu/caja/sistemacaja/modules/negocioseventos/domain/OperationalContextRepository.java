package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OperationalContextRepository {

    List<OperationalContext> findAvailableForOperation();

    List<OperationalContext> findAll();

    Page<OperationalContext> findAll(Pageable pageable);

    Optional<OperationalContext> findById(Long id);

    OperationalContext save(OperationalContext operationalContext);
}
