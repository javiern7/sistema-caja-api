package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain;

import java.util.List;
import java.util.Optional;

public interface OperationalContextRepository {

    List<OperationalContext> findAvailableForOperation();

    List<OperationalContext> findAll();

    Optional<OperationalContext> findById(Long id);

    OperationalContext save(OperationalContext operationalContext);
}
