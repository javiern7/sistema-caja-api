package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application.AuditRegistrar;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.StockLedgerService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleItemEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SalePaymentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.CreateSaleRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SalePaymentRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@Service
public class CreateSaleUseCase {

    private static final String INTERNAL_RECEIPT_SERIES = "CI";

    private final JpaSaleRepository jpaSaleRepository;
    private final JpaProductRepository jpaProductRepository;
    private final StockLedgerService stockLedgerService;
    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final JpaOperationalContextRepository jpaOperationalContextRepository;
    private final JpaUserRepository jpaUserRepository;
    private final SaleMapper saleMapper;
    private final AuditRegistrar auditRegistrar;

    public CreateSaleUseCase(
            JpaSaleRepository jpaSaleRepository,
            JpaProductRepository jpaProductRepository,
            StockLedgerService stockLedgerService,
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            JpaOperationalContextRepository jpaOperationalContextRepository,
            JpaUserRepository jpaUserRepository,
            SaleMapper saleMapper,
            AuditRegistrar auditRegistrar
    ) {
        this.jpaSaleRepository = jpaSaleRepository;
        this.jpaProductRepository = jpaProductRepository;
        this.stockLedgerService = stockLedgerService;
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.jpaOperationalContextRepository = jpaOperationalContextRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.saleMapper = saleMapper;
        this.auditRegistrar = auditRegistrar;
    }

    @Transactional
    public SaleResponse execute(SecurityUserPrincipal principal, CreateSaleRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.VENTA_SIN_ITEMS,
                    HttpStatus.BAD_REQUEST,
                    "La venta debe incluir al menos un item."
            );
        }

        Map<Long, ProductEntity> productsById = loadProducts(request.items());
        CashBoxEntity cashBox = loadAndValidateCashBox(principal, request);
        OperationalContextEntity operationalContext = cashBox.getOperationalContext();
        UserEntity seller = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        BigDecimal subtotal = BigDecimal.ZERO;
        List<SaleItemEntity> saleItems = new ArrayList<>();

        for (SaleItemRequest itemRequest : request.items()) {
            ProductEntity product = productsById.get(itemRequest.productId());

            if (!product.isActive()) {
                throw new BusinessException(
                        ErrorCode.PRODUCTO_INACTIVO,
                        HttpStatus.CONFLICT,
                        "No se puede vender un producto inactivo."
                );
            }

            BigDecimal itemSubtotal = itemRequest.unitPrice().multiply(itemRequest.quantity());
            subtotal = subtotal.add(itemSubtotal);

            SaleItemEntity saleItem = new SaleItemEntity();
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setUnitPrice(itemRequest.unitPrice());
            saleItem.setSubtotalAmount(itemSubtotal);
            saleItems.add(saleItem);

        }

        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    ErrorCode.VENTA_TOTAL_INVALIDO,
                    HttpStatus.BAD_REQUEST,
                    "El total de la venta no puede ser negativo."
            );
        }

        BigDecimal totalPayments = request.payments().stream()
                .map(SalePaymentRequest::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (subtotal.compareTo(totalPayments) != 0) {
            throw new BusinessException(
                    ErrorCode.PAGOS_NO_CUADRAN,
                    HttpStatus.BAD_REQUEST,
                    "La suma de pagos debe coincidir exactamente con el total de la venta."
            );
        }

        SaleEntity saleEntity = new SaleEntity();
        saleEntity.setOperationalContext(operationalContext);
        saleEntity.setCashBox(cashBox);
        saleEntity.setSoldBy(seller);
        saleEntity.setStatus(SaleStatus.REGISTRADA);
        saleEntity.setSubtotalAmount(subtotal);
        saleEntity.setTotalAmount(subtotal);
        saleEntity.setObservation(request.observation());
        saleEntity.setInternalReceiptSeries(INTERNAL_RECEIPT_SERIES);
        saleEntity.setInternalReceiptNumber(0L);
        saleEntity.setCreatedAt(LocalDateTime.now());

        saleItems.forEach(item -> item.setSale(saleEntity));
        saleEntity.setItems(saleItems);

        List<SalePaymentEntity> salePayments = request.payments().stream()
                .map(paymentRequest -> {
                    SalePaymentEntity paymentEntity = new SalePaymentEntity();
                    paymentEntity.setSale(saleEntity);
                    paymentEntity.setPaymentMethod(paymentRequest.paymentMethod());
                    paymentEntity.setAmount(paymentRequest.amount());
                    return paymentEntity;
                })
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        saleEntity.setPayments(salePayments);

        SaleEntity savedSale = jpaSaleRepository.save(saleEntity);
        savedSale.setInternalReceiptNumber(savedSale.getId());
        SaleEntity finalizedSale = jpaSaleRepository.save(savedSale);

        for (SaleItemEntity item : finalizedSale.getItems()) {
            if (item.getProduct().isStockControlled()) {
                stockLedgerService.decreaseStock(
                        operationalContext,
                        item.getProduct(),
                        item.getQuantity(),
                        principal.getUsername(),
                        StockMovementType.SALIDA,
                        "VENTA",
                        finalizedSale.getId().toString(),
                        "Salida por venta"
                );
            }
        }

        List<CashMovementEntity> cashMovements = request.payments().stream()
                .map(payment -> buildCashMovement(cashBox, payment, principal.getUsername(), finalizedSale.getId()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        jpaCashMovementRepository.saveAll(cashMovements);
        auditRegistrar.record(
                "VENTA",
                "REGISTRO",
                "sale",
                finalizedSale.getId().toString(),
                principal.getUsername(),
                "Venta registrada por total " + finalizedSale.getTotalAmount()
        );

        return saleMapper.toResponse(finalizedSale);
    }

    private Map<Long, ProductEntity> loadProducts(List<SaleItemRequest> items) {
        List<Long> productIds = items.stream().map(SaleItemRequest::productId).distinct().toList();
        List<ProductEntity> products = jpaProductRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new BusinessException(
                    ErrorCode.PRODUCTO_NO_ENCONTRADO,
                    HttpStatus.NOT_FOUND,
                    "Uno o mas productos no existen."
            );
        }

        Map<Long, ProductEntity> productsById = new LinkedHashMap<>();
        products.forEach(product -> productsById.put(product.getId(), product));
        return productsById;
    }

    private CashBoxEntity loadAndValidateCashBox(SecurityUserPrincipal principal, CreateSaleRequest request) {
        CashBoxEntity cashBox = jpaCashBoxRepository.findById(request.cashBoxId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CAJA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la caja solicitada."
                ));

        if (!cashBox.getStatus().equals(CashBoxStatus.ABIERTA)
                || !cashBox.getOpenedBy().getId().equals(principal.toAuthenticatedUser().id())
                || !cashBox.getOperationalContext().getId().equals(request.operationalContextId())) {
            throw new BusinessException(
                    ErrorCode.VENTA_CAJA_INVALIDA,
                    HttpStatus.CONFLICT,
                    "La venta requiere una caja abierta y valida para el usuario y contexto."
            );
        }

        jpaOperationalContextRepository.findById(request.operationalContextId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NEGOCIO_EVENTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el contexto operativo solicitado."
                ));

        return cashBox;
    }

    private CashMovementEntity buildCashMovement(
            CashBoxEntity cashBox,
            SalePaymentRequest paymentRequest,
            String username,
            Long saleId
    ) {
        CashMovementEntity movementEntity = new CashMovementEntity();
        movementEntity.setCashBox(cashBox);
        movementEntity.setMovementType(CashMovementType.VENTA);
        movementEntity.setAmount(paymentRequest.amount());
        movementEntity.setReferenceType("VENTA");
        movementEntity.setReferenceId(saleId.toString());
        movementEntity.setPerformedBy(username);
        movementEntity.setOccurredAt(LocalDateTime.now());
        movementEntity.setObservation("Ingreso por venta - " + paymentRequest.paymentMethod());
        return movementEntity;
    }
}
