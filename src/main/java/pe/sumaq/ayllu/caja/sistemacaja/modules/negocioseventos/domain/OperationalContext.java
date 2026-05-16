package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain;

import java.time.LocalDate;

public record OperationalContext(
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
