package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseItemResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;

@Component
public class PurchaseMapper {

    public PurchaseResponse toResponse(PurchaseEntity purchaseEntity) {
        return new PurchaseResponse(
                purchaseEntity.getId(),
                purchaseEntity.getOperationalContext().getId(),
                purchaseEntity.getOperationalContext().getName(),
                purchaseEntity.getProvider().getId(),
                purchaseEntity.getProvider().getName(),
                purchaseEntity.getStatus(),
                purchaseEntity.getPurchaseDate(),
                purchaseEntity.getDocumentType(),
                purchaseEntity.getDocumentNumber(),
                purchaseEntity.getPaymentMethod(),
                purchaseEntity.getSubtotalAmount(),
                purchaseEntity.getTotalAmount(),
                purchaseEntity.getObservation(),
                purchaseEntity.getCreatedAt(),
                purchaseEntity.getCancelledAt(),
                purchaseEntity.getCancelledBy() != null ? purchaseEntity.getCancelledBy().getUsername() : null,
                purchaseEntity.getCancellationReason(),
                purchaseEntity.getItems().stream()
                        .map(item -> new PurchaseItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getCode(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getCancelledQuantity(),
                                item.getUnitCost(),
                                item.getSubtotalAmount()
                        ))
                        .toList()
        );
    }
}
