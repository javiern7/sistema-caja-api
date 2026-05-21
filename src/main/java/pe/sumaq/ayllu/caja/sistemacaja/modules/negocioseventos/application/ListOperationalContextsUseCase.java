package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextRepository;

@Service
public class ListOperationalContextsUseCase {

    private final OperationalContextRepository operationalContextRepository;

    public ListOperationalContextsUseCase(OperationalContextRepository operationalContextRepository) {
        this.operationalContextRepository = operationalContextRepository;
    }

    public Page<OperationalContext> execute(Pageable pageable) {
        return operationalContextRepository.findAll(pageable);
    }
}
