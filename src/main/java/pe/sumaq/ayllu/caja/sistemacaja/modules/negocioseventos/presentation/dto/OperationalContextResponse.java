package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto;

import java.time.LocalDate;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;

public record OperationalContextResponse(
        Long id,
        String code,
        String name,
        OperationalContextType type,
        OperationalContextStatus status,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
