package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateSaleRequest(
        @NotNull(message = "El contexto operativo es obligatorio.")
        Long operationalContextId,
        @NotNull(message = "La caja es obligatoria.")
        Long cashBoxId,
        @NotEmpty(message = "La venta debe incluir al menos un item.")
        List<@Valid SaleItemRequest> items,
        @NotEmpty(message = "La venta debe incluir al menos un pago.")
        List<@Valid SalePaymentRequest> payments,
        String observation
) {
}
