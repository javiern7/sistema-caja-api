package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreatePurchaseRequest(
        @NotNull(message = "El contexto operativo es obligatorio.")
        Long operationalContextId,
        @NotNull(message = "El proveedor es obligatorio.")
        Long providerId,
        @NotNull(message = "La fecha de compra es obligatoria.")
        LocalDate purchaseDate,
        String documentType,
        String documentNumber,
        String paymentMethod,
        @NotEmpty(message = "La compra debe incluir al menos un item.")
        List<@Valid PurchaseItemRequest> items,
        String observation
) {
}
