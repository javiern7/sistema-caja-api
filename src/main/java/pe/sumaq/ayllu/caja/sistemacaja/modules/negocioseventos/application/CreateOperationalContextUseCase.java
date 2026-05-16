package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.CreateOperationalContextRequest;

@Service
public class CreateOperationalContextUseCase {

    private final OperationalContextRepository operationalContextRepository;
    private final OperationalContextValidator operationalContextValidator;

    public CreateOperationalContextUseCase(
            OperationalContextRepository operationalContextRepository,
            OperationalContextValidator operationalContextValidator
    ) {
        this.operationalContextRepository = operationalContextRepository;
        this.operationalContextValidator = operationalContextValidator;
    }

    public OperationalContext execute(CreateOperationalContextRequest request) {
        operationalContextValidator.validateDates(request.startDate(), request.endDate());

        return operationalContextRepository.save(new OperationalContext(
                null,
                request.code(),
                request.name(),
                request.type(),
                request.status(),
                request.startDate(),
                request.endDate(),
                request.description()
        ));
    }
}
