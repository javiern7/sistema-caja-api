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
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application.AuditRegistrar;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseItemEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CancelPurchaseItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CancelPurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.StockLedgerService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class CancelPurchaseUseCase {

    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final StockLedgerService stockLedgerService;
    private final JpaUserRepository jpaUserRepository;
    private final PurchaseMapper purchaseMapper;
    private final AuditRegistrar auditRegistrar;

    public CancelPurchaseUseCase(
            JpaPurchaseRepository jpaPurchaseRepository,
            StockLedgerService stockLedgerService,
            JpaUserRepository jpaUserRepository,
            PurchaseMapper purchaseMapper,
            AuditRegistrar auditRegistrar
    ) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.stockLedgerService = stockLedgerService;
        this.jpaUserRepository = jpaUserRepository;
        this.purchaseMapper = purchaseMapper;
        this.auditRegistrar = auditRegistrar;
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
                            "La cantidad anulada no puede exceder la cantidad pendiente del item.",
                            java.util.List.of(
                                    "purchaseItemId=" + item.getId(),
                                    "remainingQuantity=" + remaining,
                                    "requestedCancelledQuantity=" + cancelRequest.cancelledQuantity()
                            )
                    );
                }

                item.setCancelledQuantity(item.getCancelledQuantity().add(cancelRequest.cancelledQuantity()));

                if (item.getProduct().isStockControlled()) {
                    stockLedgerService.decreaseStock(
                            purchaseEntity.getOperationalContext(),
                            item.getProduct(),
                            cancelRequest.cancelledQuantity(),
                            principal.getUsername(),
                            StockMovementType.REVERSA,
                            "COMPRA_ANULADA",
                            purchaseEntity.getId().toString(),
                            "Reversa por anulacion de compra"
                    );
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
        auditRegistrar.record(
                "COMPRA",
                "ANULACION",
                "purchase",
                purchaseEntity.getId().toString(),
                principal.getUsername(),
                request.reason()
        );

        return purchaseMapper.toResponse(jpaPurchaseRepository.save(purchaseEntity));
    }
}
