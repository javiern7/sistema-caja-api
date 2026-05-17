package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;

public record PurchaseResponse(
        Long id,
        Long operationalContextId,
        String operationalContextName,
        Long providerId,
        String providerName,
        PurchaseStatus status,
        LocalDate purchaseDate,
        String documentType,
        String documentNumber,
        String paymentMethod,
        BigDecimal subtotalAmount,
        BigDecimal totalAmount,
        String observation,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt,
        String cancelledByUsername,
        String cancellationReason,
        List<PurchaseItemResponse> items
) {
}
