package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;

@Component
public class OperationalContextValidator {

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException(
                    ErrorCode.NEGOCIO_EVENTO_FECHAS_INVALIDAS,
                    HttpStatus.BAD_REQUEST,
                    "La fecha fin no puede ser menor que la fecha inicio."
            );
        }
    }

    public void validateTransition(
            OperationalContextStatus currentStatus,
            OperationalContextStatus targetStatus
    ) {
        boolean validTransition = switch (currentStatus) {
            case PLANIFICADO -> targetStatus == OperationalContextStatus.PLANIFICADO
                    || targetStatus == OperationalContextStatus.EN_CURSO
                    || targetStatus == OperationalContextStatus.CANCELADO;
            case EN_CURSO -> targetStatus == OperationalContextStatus.EN_CURSO
                    || targetStatus == OperationalContextStatus.CERRADO
                    || targetStatus == OperationalContextStatus.CANCELADO;
            case CERRADO -> targetStatus == OperationalContextStatus.CERRADO;
            case CANCELADO -> targetStatus == OperationalContextStatus.CANCELADO;
        };

        if (!validTransition) {
            throw new BusinessException(
                    ErrorCode.NEGOCIO_EVENTO_TRANSICION_INVALIDA,
                    HttpStatus.BAD_REQUEST,
                    "La transicion de estado del negocio/evento no es valida."
            );
        }
    }
}
