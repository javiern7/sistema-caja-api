package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;

public record UpdateOperationalContextRequest(
        @NotBlank(message = "El codigo es obligatorio.")
        String code,
        @NotBlank(message = "El nombre es obligatorio.")
        String name,
        @NotNull(message = "El tipo operativo es obligatorio.")
        OperationalContextType type,
        @NotNull(message = "El estado es obligatorio.")
        OperationalContextStatus status,
        @NotNull(message = "La fecha inicio es obligatoria.")
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
