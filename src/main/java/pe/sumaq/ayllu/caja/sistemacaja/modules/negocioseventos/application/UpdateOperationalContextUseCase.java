package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.UpdateOperationalContextRequest;

@Service
public class UpdateOperationalContextUseCase {

    private final OperationalContextRepository operationalContextRepository;
    private final OperationalContextValidator operationalContextValidator;

    public UpdateOperationalContextUseCase(
            OperationalContextRepository operationalContextRepository,
            OperationalContextValidator operationalContextValidator
    ) {
        this.operationalContextRepository = operationalContextRepository;
        this.operationalContextValidator = operationalContextValidator;
    }

    public OperationalContext execute(Long operationalContextId, UpdateOperationalContextRequest request) {
        OperationalContext current = operationalContextRepository.findById(operationalContextId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NEGOCIO_EVENTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el negocio/evento solicitado."
                ));

        operationalContextValidator.validateDates(request.startDate(), request.endDate());
        operationalContextValidator.validateTransition(current.status(), request.status());

        return operationalContextRepository.save(new OperationalContext(
                current.id(),
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
