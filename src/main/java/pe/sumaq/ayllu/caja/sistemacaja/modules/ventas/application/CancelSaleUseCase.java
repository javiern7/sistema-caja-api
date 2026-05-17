package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleItemEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.CancelSaleRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@Service
public class CancelSaleUseCase {

    private final JpaSaleRepository jpaSaleRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaStockCurrentRepository jpaStockCurrentRepository;
    private final JpaStockMovementRepository jpaStockMovementRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final SaleMapper saleMapper;

    public CancelSaleUseCase(
            JpaSaleRepository jpaSaleRepository,
            JpaUserRepository jpaUserRepository,
            JpaStockCurrentRepository jpaStockCurrentRepository,
            JpaStockMovementRepository jpaStockMovementRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            SaleMapper saleMapper
    ) {
        this.jpaSaleRepository = jpaSaleRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
        this.jpaStockMovementRepository = jpaStockMovementRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.saleMapper = saleMapper;
    }

    @Transactional
    public SaleResponse execute(Long saleId, SecurityUserPrincipal principal, CancelSaleRequest request) {
        SaleEntity saleEntity = jpaSaleRepository.findById(saleId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.VENTA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la venta solicitada."
                ));

        if (!saleEntity.getStatus().isRegistered()) {
            throw new BusinessException(
                    ErrorCode.VENTA_NO_ANULABLE,
                    HttpStatus.CONFLICT,
                    "La venta no se encuentra en un estado anulable."
            );
        }

        UserEntity cancelledBy = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        List<StockCurrentEntity> stockUpdates = new ArrayList<>();
        List<StockMovementEntity> stockMovements = new ArrayList<>();

        for (SaleItemEntity item : saleEntity.getItems()) {
            if (item.getProduct().isStockControlled()) {
                StockCurrentEntity stockCurrent = jpaStockCurrentRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.PRODUCTO_NO_ENCONTRADO,
                                HttpStatus.NOT_FOUND,
                                "No se encontro el stock del producto afectado."
                        ));

                stockCurrent.setCurrentStock(stockCurrent.getCurrentStock().add(item.getQuantity()));
                stockCurrent.setUpdatedAt(LocalDateTime.now());
                stockUpdates.add(stockCurrent);

                StockMovementEntity stockMovement = new StockMovementEntity();
                stockMovement.setProduct(item.getProduct());
                stockMovement.setMovementType(StockMovementType.REVERSA);
                stockMovement.setQuantity(item.getQuantity());
                stockMovement.setReferenceType("VENTA_ANULADA");
                stockMovement.setReferenceId(saleEntity.getId().toString());
                stockMovement.setPerformedBy(principal.getUsername());
                stockMovement.setOccurredAt(LocalDateTime.now());
                stockMovement.setNote("Reposicion por anulacion de venta");
                stockMovements.add(stockMovement);
            }
        }

        List<CashMovementEntity> cashMovements = saleEntity.getPayments().stream()
                .map(payment -> {
                    CashMovementEntity movement = new CashMovementEntity();
                    movement.setCashBox(saleEntity.getCashBox());
                    movement.setMovementType(CashMovementType.ANULACION_VENTA);
                    movement.setAmount(payment.getAmount().negate());
                    movement.setReferenceType("VENTA_ANULADA");
                    movement.setReferenceId(saleEntity.getId().toString());
                    movement.setPerformedBy(principal.getUsername());
                    movement.setOccurredAt(LocalDateTime.now());
                    movement.setObservation("Reversa de venta - " + payment.getPaymentMethod());
                    return movement;
                })
                .toList();

        saleEntity.setStatus(SaleStatus.ANULADA);
        saleEntity.setCancelledAt(LocalDateTime.now());
        saleEntity.setCancelledBy(cancelledBy);
        saleEntity.setCancellationReason(request.reason());

        jpaStockCurrentRepository.saveAll(stockUpdates);
        jpaStockMovementRepository.saveAll(stockMovements);
        jpaCashMovementRepository.saveAll(cashMovements);

        return saleMapper.toResponse(jpaSaleRepository.save(saleEntity));
    }
}
