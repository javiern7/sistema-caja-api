package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleItemResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SalePaymentResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@Component
public class SaleMapper {

    public SaleResponse toResponse(SaleEntity saleEntity) {
        return new SaleResponse(
                saleEntity.getId(),
                saleEntity.getOperationalContext().getId(),
                saleEntity.getOperationalContext().getName(),
                saleEntity.getCashBox().getId(),
                saleEntity.getSoldBy().getUsername(),
                saleEntity.getStatus(),
                saleEntity.getSubtotalAmount(),
                saleEntity.getTotalAmount(),
                saleEntity.getInternalReceiptSeries(),
                saleEntity.getInternalReceiptNumber(),
                saleEntity.getObservation(),
                saleEntity.getCreatedAt(),
                saleEntity.getCancelledAt(),
                saleEntity.getCancelledBy() != null ? saleEntity.getCancelledBy().getUsername() : null,
                saleEntity.getCancellationReason(),
                saleEntity.getItems().stream()
                        .map(item -> new SaleItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getCode(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotalAmount()
                        ))
                        .toList(),
                saleEntity.getPayments().stream()
                        .map(payment -> new SalePaymentResponse(
                                payment.getId(),
                                payment.getPaymentMethod(),
                                payment.getAmount()
                        ))
                        .toList()
        );
    }
}
