package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CreatePurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.StockLedgerService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class CreatePurchaseUseCase {

    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final JpaOperationalContextRepository jpaOperationalContextRepository;
    private final JpaProviderRepository jpaProviderRepository;
    private final JpaProductRepository jpaProductRepository;
    private final StockLedgerService stockLedgerService;
    private final JpaUserRepository jpaUserRepository;
    private final PurchaseMapper purchaseMapper;
    private final AuditRegistrar auditRegistrar;

    public CreatePurchaseUseCase(
            JpaPurchaseRepository jpaPurchaseRepository,
            JpaOperationalContextRepository jpaOperationalContextRepository,
            JpaProviderRepository jpaProviderRepository,
            JpaProductRepository jpaProductRepository,
            StockLedgerService stockLedgerService,
            JpaUserRepository jpaUserRepository,
            PurchaseMapper purchaseMapper,
            AuditRegistrar auditRegistrar
    ) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.jpaOperationalContextRepository = jpaOperationalContextRepository;
        this.jpaProviderRepository = jpaProviderRepository;
        this.jpaProductRepository = jpaProductRepository;
        this.stockLedgerService = stockLedgerService;
        this.jpaUserRepository = jpaUserRepository;
        this.purchaseMapper = purchaseMapper;
        this.auditRegistrar = auditRegistrar;
    }

    @Transactional
    public PurchaseResponse execute(SecurityUserPrincipal principal, CreatePurchaseRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.COMPRA_SIN_ITEMS,
                    HttpStatus.BAD_REQUEST,
                    "La compra debe incluir al menos un item."
            );
        }

        OperationalContextEntity operationalContext = jpaOperationalContextRepository.findById(request.operationalContextId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NEGOCIO_EVENTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el contexto operativo solicitado."
                ));

        ProviderEntity provider = jpaProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROVEEDOR_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el proveedor solicitado."
                ));

        if (!provider.isActive()) {
            throw new BusinessException(
                    ErrorCode.COMPRA_PROVEEDOR_INACTIVO,
                    HttpStatus.CONFLICT,
                    "No se puede registrar una compra con proveedor inactivo."
            );
        }

        UserEntity purchaser = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        Map<Long, ProductEntity> productsById = loadProducts(request.items());
        BigDecimal subtotal = BigDecimal.ZERO;
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setOperationalContext(operationalContext);
        purchaseEntity.setProvider(provider);
        purchaseEntity.setPurchasedBy(purchaser);
        purchaseEntity.setStatus(PurchaseStatus.REGISTRADA);
        purchaseEntity.setPurchaseDate(request.purchaseDate());
        purchaseEntity.setDocumentType(request.documentType());
        purchaseEntity.setDocumentNumber(request.documentNumber());
        purchaseEntity.setPaymentMethod(request.paymentMethod());
        purchaseEntity.setObservation(request.observation());
        purchaseEntity.setCreatedAt(LocalDateTime.now());

        List<PurchaseItemEntity> purchaseItems = request.items().stream()
                .map(itemRequest -> {
                    ProductEntity product = productsById.get(itemRequest.productId());
                    BigDecimal itemSubtotal = itemRequest.quantity().multiply(itemRequest.unitCost());
                    PurchaseItemEntity itemEntity = new PurchaseItemEntity();
                    itemEntity.setPurchase(purchaseEntity);
                    itemEntity.setProduct(product);
                    itemEntity.setQuantity(itemRequest.quantity());
                    itemEntity.setCancelledQuantity(BigDecimal.ZERO);
                    itemEntity.setUnitCost(itemRequest.unitCost());
                    itemEntity.setSubtotalAmount(itemSubtotal);
                    return itemEntity;
                })
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        for (PurchaseItemEntity item : purchaseItems) {
            subtotal = subtotal.add(item.getSubtotalAmount());
        }

        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(
                    ErrorCode.COMPRA_TOTAL_INVALIDA,
                    HttpStatus.BAD_REQUEST,
                    "El total de la compra no puede ser negativo."
            );
        }

        purchaseEntity.setSubtotalAmount(subtotal);
        purchaseEntity.setTotalAmount(subtotal);
        purchaseEntity.setItems(purchaseItems);

        PurchaseEntity savedPurchase = jpaPurchaseRepository.save(purchaseEntity);

        for (PurchaseItemEntity item : savedPurchase.getItems()) {
            if (item.getProduct().isStockControlled()) {
                stockLedgerService.increaseStock(
                        operationalContext,
                        item.getProduct(),
                        item.getQuantity(),
                        principal.getUsername(),
                        StockMovementType.ENTRADA,
                        "COMPRA",
                        savedPurchase.getId().toString(),
                        "Entrada por compra"
                );
            }
        }
        auditRegistrar.record(
                "COMPRA",
                "REGISTRO",
                "purchase",
                savedPurchase.getId().toString(),
                principal.getUsername(),
                "Compra registrada por total " + savedPurchase.getTotalAmount()
        );

        return purchaseMapper.toResponse(savedPurchase);
    }

    private Map<Long, ProductEntity> loadProducts(List<PurchaseItemRequest> items) {
        List<Long> productIds = items.stream().map(PurchaseItemRequest::productId).distinct().toList();
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
}
