package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;

public record PurchaseListResponse(
        Long id,
        Long operationalContextId,
        String operationalContextName,
        Long providerId,
        String providerName,
        String purchasedByUsername,
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
        int itemsCount
) {
}
