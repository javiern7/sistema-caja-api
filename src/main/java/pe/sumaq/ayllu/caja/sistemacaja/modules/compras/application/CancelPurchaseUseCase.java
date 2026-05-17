package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseItemEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CancelPurchaseItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CancelPurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class CancelPurchaseUseCase {

    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final JpaStockCurrentRepository jpaStockCurrentRepository;
    private final JpaStockMovementRepository jpaStockMovementRepository;
    private final JpaUserRepository jpaUserRepository;
    private final PurchaseMapper purchaseMapper;

    public CancelPurchaseUseCase(
            JpaPurchaseRepository jpaPurchaseRepository,
            JpaStockCurrentRepository jpaStockCurrentRepository,
            JpaStockMovementRepository jpaStockMovementRepository,
            JpaUserRepository jpaUserRepository,
            PurchaseMapper purchaseMapper
    ) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
        this.jpaStockMovementRepository = jpaStockMovementRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Transactional
    public PurchaseResponse execute(Long purchaseId, SecurityUserPrincipal principal, CancelPurchaseRequest request) {
        PurchaseEntity purchaseEntity = jpaPurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.COMPRA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la compra solicitada."
                ));

        if (purchaseEntity.getStatus() == PurchaseStatus.ANULADA) {
            throw new BusinessException(
                    ErrorCode.COMPRA_ANULACION_INVALIDA,
                    HttpStatus.CONFLICT,
                    "La compra ya se encuentra totalmente anulada."
            );
        }

        UserEntity cancelledBy = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        Map<Long, CancelPurchaseItemRequest> requestedItems = new HashMap<>();
        request.cancelledItems().forEach(item -> requestedItems.put(item.purchaseItemId(), item));

        boolean allFullyCancelled = true;

        for (PurchaseItemEntity item : purchaseEntity.getItems()) {
            CancelPurchaseItemRequest cancelRequest = requestedItems.get(item.getId());

            BigDecimal remaining = item.getQuantity().subtract(item.getCancelledQuantity());
            if (cancelRequest != null) {
                if (cancelRequest.cancelledQuantity().compareTo(remaining) > 0) {
                    throw new BusinessException(
                            ErrorCode.COMPRA_ANULACION_INVALIDA,
                            HttpStatus.BAD_REQUEST,
                            "La cantidad anulada no puede exceder la cantidad pendiente del item."
                    );
                }

                item.setCancelledQuantity(item.getCancelledQuantity().add(cancelRequest.cancelledQuantity()));

                if (item.getProduct().isStockControlled()) {
                    StockCurrentEntity stockCurrent = jpaStockCurrentRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new BusinessException(
                                    ErrorCode.PRODUCTO_NO_ENCONTRADO,
                                    HttpStatus.NOT_FOUND,
                                    "No se encontro el stock del producto afectado."
                            ));

                    stockCurrent.setCurrentStock(stockCurrent.getCurrentStock().subtract(cancelRequest.cancelledQuantity()));
                    stockCurrent.setUpdatedAt(LocalDateTime.now());
                    jpaStockCurrentRepository.save(stockCurrent);

                    StockMovementEntity movement = new StockMovementEntity();
                    movement.setProduct(item.getProduct());
                    movement.setMovementType(StockMovementType.REVERSA);
                    movement.setQuantity(cancelRequest.cancelledQuantity());
                    movement.setReferenceType("COMPRA_ANULADA");
                    movement.setReferenceId(purchaseEntity.getId().toString());
                    movement.setPerformedBy(principal.getUsername());
                    movement.setOccurredAt(LocalDateTime.now());
                    movement.setNote("Reversa por anulacion de compra");
                    jpaStockMovementRepository.save(movement);
                }
            }

            if (item.getCancelledQuantity().compareTo(item.getQuantity()) < 0) {
                allFullyCancelled = false;
            }
        }

        purchaseEntity.setStatus(allFullyCancelled ? PurchaseStatus.ANULADA : PurchaseStatus.ANULADA_PARCIAL);
        purchaseEntity.setCancelledAt(LocalDateTime.now());
        purchaseEntity.setCancelledBy(cancelledBy);
        purchaseEntity.setCancellationReason(request.reason());

        return purchaseMapper.toResponse(jpaPurchaseRepository.save(purchaseEntity));
    }
}
