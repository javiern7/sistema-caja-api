package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.domain.ExpenseType;

public record CreateExpenseRequest(
        @NotNull(message = "El contexto operativo es obligatorio.")
        Long operationalContextId,
        Long cashBoxId,
        @NotNull(message = "El tipo de egreso es obligatorio.")
        ExpenseType expenseType,
        @NotBlank(message = "La categoria es obligatoria.")
        String category,
        @NotBlank(message = "La descripcion es obligatoria.")
        String description,
        String paymentMethod,
        @NotNull(message = "El monto es obligatorio.")
        @DecimalMin(value = "0.00", message = "El monto no puede ser negativo.")
        BigDecimal amount,
        String responsible,
        String observation,
        @NotNull(message = "La fecha del egreso es obligatoria.")
        LocalDate expenseDate
) {
}
