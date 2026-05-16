package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextRepository;

@Service
public class ListAvailableOperationalContextsUseCase {

    private final OperationalContextRepository operationalContextRepository;

    public ListAvailableOperationalContextsUseCase(OperationalContextRepository operationalContextRepository) {
        this.operationalContextRepository = operationalContextRepository;
    }

    public List<OperationalContext> execute() {
        return operationalContextRepository.findAvailableForOperation();
    }
}
