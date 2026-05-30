package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxReportSpecifications;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseReportSpecifications;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.ExpenseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.ExpenseReportSpecifications;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.CashReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.CashReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ExpenseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ExpenseReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.PurchaseReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.PurchaseReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportRowResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.UtilityReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleReportSpecifications;

@Service
public class ReportsQueryService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final JpaSaleRepository jpaSaleRepository;
    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final JpaExpenseRepository jpaExpenseRepository;
    private final JpaStockCurrentRepository jpaStockCurrentRepository;
    private final JpaProductRepository jpaProductRepository;
    private final ReportHistoryRegistrar reportHistoryRegistrar;

    public ReportsQueryService(
            JpaSaleRepository jpaSaleRepository,
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaPurchaseRepository jpaPurchaseRepository,
            JpaExpenseRepository jpaExpenseRepository,
            JpaStockCurrentRepository jpaStockCurrentRepository,
            JpaProductRepository jpaProductRepository,
            ReportHistoryRegistrar reportHistoryRegistrar
    ) {
        this.jpaSaleRepository = jpaSaleRepository;
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.jpaExpenseRepository = jpaExpenseRepository;
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
        this.jpaProductRepository = jpaProductRepository;
        this.reportHistoryRegistrar = reportHistoryRegistrar;
    }

    @Transactional(readOnly = true)
    public SalesReportResponse getSalesReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            String username,
            ReportFormat format
    ) {
        List<SaleEntity> sales = jpaSaleRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> item.getStatus() == SaleStatus.REGISTRADA)
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getCreatedAt(), fechaDesde, fechaHasta))
                .toList();

        List<SalesReportRowResponse> rows = sales.stream()
                .map(item -> new SalesReportRowResponse(
                        item.getId(),
                        item.getCreatedAt(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getSoldBy().getUsername(),
                        item.getInternalReceiptSeries() + "-" + item.getInternalReceiptNumber(),
                        item.getTotalAmount(),
                        item.getItems().size()
                ))
                .toList();

        SalesReportResponse response = new SalesReportResponse(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                rows.size(),
                rows.stream().map(SalesReportRowResponse::totalAmount).reduce(ZERO, BigDecimal::add),
                rows
        );
        reportHistoryRegistrar.record(ReportType.VENTAS, format, username, fechaDesde, fechaHasta, operationalContextId);
        return response;
    }

    @Transactional(readOnly = true)
    public CashReportResponse getCashReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            String username,
            ReportFormat format
    ) {
        List<CashBoxEntity> cashBoxes = jpaCashBoxRepository.findAllByOrderByOpenedAtDesc().stream()
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getOpenedAt(), fechaDesde, fechaHasta))
                .toList();

        List<CashReportRowResponse> rows = cashBoxes.stream()
                .map(item -> new CashReportRowResponse(
                        item.getId(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getOpenedBy().getUsername(),
                        item.getClosedBy() == null ? null : item.getClosedBy().getUsername(),
                        item.getStatus(),
                        safe(item.getOpeningAmount()),
                        safe(item.getExpectedAmount()),
                        safe(item.getCountedAmount()),
                        safe(item.getDifferenceAmount()),
                        item.getOpenedAt(),
                        item.getClosedAt()
                ))
                .toList();

        CashReportResponse response = new CashReportResponse(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                rows.size(),
                rows.stream().map(CashReportRowResponse::openingAmount).reduce(ZERO, BigDecimal::add),
                rows.stream().map(CashReportRowResponse::expectedAmount).reduce(ZERO, BigDecimal::add),
                rows.stream().map(CashReportRowResponse::differenceAmount).reduce(ZERO, BigDecimal::add),
                rows
        );
        reportHistoryRegistrar.record(ReportType.CAJA, format, username, fechaDesde, fechaHasta, operationalContextId);
        return response;
    }

    @Transactional(readOnly = true)
    public PurchaseReportResponse getPurchasesReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            String username,
            ReportFormat format
    ) {
        List<PurchaseEntity> purchases = jpaPurchaseRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> item.getStatus() != PurchaseStatus.ANULADA)
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getPurchaseDate(), fechaDesde, fechaHasta))
                .toList();

        List<PurchaseReportRowResponse> rows = purchases.stream()
                .map(item -> new PurchaseReportRowResponse(
                        item.getId(),
                        item.getPurchaseDate(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getProvider().getName(),
                        item.getStatus().name(),
                        calculateEffectivePurchaseAmount(item)
                ))
                .toList();

        PurchaseReportResponse response = new PurchaseReportResponse(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                rows.size(),
                rows.stream().map(PurchaseReportRowResponse::effectiveAmount).reduce(ZERO, BigDecimal::add),
                rows
        );
        reportHistoryRegistrar.record(ReportType.COMPRAS, format, username, fechaDesde, fechaHasta, operationalContextId);
        return response;
    }

    @Transactional(readOnly = true)
    public ExpenseReportResponse getExpensesReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            String username,
            ReportFormat format
    ) {
        List<ExpenseEntity> expenses = jpaExpenseRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getExpenseDate(), fechaDesde, fechaHasta))
                .toList();

        List<ExpenseReportRowResponse> rows = expenses.stream()
                .map(item -> new ExpenseReportRowResponse(
                        item.getId(),
                        item.getExpenseDate(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getExpenseType().name(),
                        item.getCategory(),
                        item.getDescription(),
                        item.getAmount(),
                        item.getRecordedBy().getUsername()
                ))
                .toList();

        ExpenseReportResponse response = new ExpenseReportResponse(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                rows.size(),
                rows.stream().map(ExpenseReportRowResponse::amount).reduce(ZERO, BigDecimal::add),
                rows
        );
        reportHistoryRegistrar.record(ReportType.EGRESOS, format, username, fechaDesde, fechaHasta, operationalContextId);
        return response;
    }

    @Transactional(readOnly = true)
    public StockReportResponse getStockReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            String username,
            ReportFormat format
    ) {
        List<StockReportRowResponse> rows = jpaStockCurrentRepository.findAllByOperationalContextId(operationalContextId).stream()
                .map(item -> toStockRow(item.getProduct(), item))
                .toList();

        StockReportResponse response = new StockReportResponse(
                "OPERATIONAL_CONTEXT",
                operationalContextId,
                true,
                rows.size(),
                rows.stream().map(StockReportRowResponse::currentStock).reduce(ZERO, BigDecimal::add),
                rows
        );
        reportHistoryRegistrar.record(ReportType.STOCK, format, username, fechaDesde, fechaHasta, operationalContextId);
        return response;
    }

    @Transactional(readOnly = true)
    public UtilityReportResponse getUtilityReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            String username,
            ReportFormat format
    ) {
        List<SaleEntity> sales = jpaSaleRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> item.getStatus() == SaleStatus.REGISTRADA)
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getCreatedAt(), fechaDesde, fechaHasta))
                .toList();

        List<PurchaseEntity> purchases = jpaPurchaseRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> item.getStatus() != PurchaseStatus.ANULADA)
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getPurchaseDate(), fechaDesde, fechaHasta))
                .toList();

        List<ExpenseEntity> expenses = jpaExpenseRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> matchesContext(item.getOperationalContext().getId(), operationalContextId))
                .filter(item -> matchesDate(item.getExpenseDate(), fechaDesde, fechaHasta))
                .toList();

        BigDecimal salesAmount = sales.stream().map(SaleEntity::getTotalAmount).reduce(ZERO, BigDecimal::add);
        BigDecimal purchaseAmount = purchases.stream().map(this::calculateEffectivePurchaseAmount).reduce(ZERO, BigDecimal::add);
        BigDecimal expenseAmount = expenses.stream().map(ExpenseEntity::getAmount).reduce(ZERO, BigDecimal::add);
        BigDecimal estimatedCostOfSales = sales.stream()
                .flatMap(item -> item.getItems().stream())
                .map(item -> item.getProduct().getReferenceCost().multiply(item.getQuantity()))
                .reduce(ZERO, BigDecimal::add);
        BigDecimal grossMargin = salesAmount.subtract(estimatedCostOfSales);
        BigDecimal netUtility = grossMargin.subtract(expenseAmount);

        UtilityReportResponse response = new UtilityReportResponse(
                fechaDesde,
                fechaHasta,
                operationalContextId,
                salesAmount,
                purchaseAmount,
                expenseAmount,
                estimatedCostOfSales,
                grossMargin,
                netUtility
        );
        reportHistoryRegistrar.record(ReportType.UTILIDAD, format, username, fechaDesde, fechaHasta, operationalContextId);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<SalesReportRowResponse> getSalesReportPage(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            Pageable pageable
    ) {
        return jpaSaleRepository.findAll(
                        SaleReportSpecifications.forReport(fechaDesde, fechaHasta, operationalContextId),
                        pageable
                )
                .map(item -> new SalesReportRowResponse(
                        item.getId(),
                        item.getCreatedAt(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getSoldBy().getUsername(),
                        item.getInternalReceiptSeries() + "-" + item.getInternalReceiptNumber(),
                        item.getTotalAmount(),
                        item.getItems().size()
                ));
    }

    @Transactional(readOnly = true)
    public Page<CashReportRowResponse> getCashReportPage(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            Pageable pageable
    ) {
        return jpaCashBoxRepository.findAll(
                        CashBoxReportSpecifications.forReport(fechaDesde, fechaHasta, operationalContextId),
                        pageable
                )
                .map(item -> new CashReportRowResponse(
                        item.getId(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getOpenedBy().getUsername(),
                        item.getClosedBy() == null ? null : item.getClosedBy().getUsername(),
                        item.getStatus(),
                        safe(item.getOpeningAmount()),
                        safe(item.getExpectedAmount()),
                        safe(item.getCountedAmount()),
                        safe(item.getDifferenceAmount()),
                        item.getOpenedAt(),
                        item.getClosedAt()
                ));
    }

    @Transactional(readOnly = true)
    public Page<PurchaseReportRowResponse> getPurchasesReportPage(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            Pageable pageable
    ) {
        return jpaPurchaseRepository.findAll(
                        PurchaseReportSpecifications.forReport(fechaDesde, fechaHasta, operationalContextId),
                        pageable
                )
                .map(item -> new PurchaseReportRowResponse(
                        item.getId(),
                        item.getPurchaseDate(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getProvider().getName(),
                        item.getStatus().name(),
                        calculateEffectivePurchaseAmount(item)
                ));
    }

    @Transactional(readOnly = true)
    public Page<ExpenseReportRowResponse> getExpensesReportPage(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId,
            Pageable pageable
    ) {
        return jpaExpenseRepository.findAll(
                        ExpenseReportSpecifications.forReport(fechaDesde, fechaHasta, operationalContextId),
                        pageable
                )
                .map(item -> new ExpenseReportRowResponse(
                        item.getId(),
                        item.getExpenseDate(),
                        item.getOperationalContext().getId(),
                        item.getOperationalContext().getName(),
                        item.getExpenseType().name(),
                        item.getCategory(),
                        item.getDescription(),
                        item.getAmount(),
                        item.getRecordedBy().getUsername()
                ));
    }

    @Transactional(readOnly = true)
    public Page<StockReportRowResponse> getStockReportPage(Long operationalContextId, Pageable pageable) {
        Page<ProductEntity> productsPage = jpaProductRepository.findAll(pageable);
        List<Long> productIds = productsPage.getContent().stream()
                .map(ProductEntity::getId)
                .toList();

        java.util.Map<Long, StockCurrentEntity> stockByProductId = jpaStockCurrentRepository.findAllByOperationalContextIdAndProductIdIn(operationalContextId, productIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(StockCurrentEntity::getProductId, item -> item));

        List<StockReportRowResponse> rows = productsPage.getContent().stream()
                .map(product -> toStockRow(product, stockByProductId.get(product.getId())))
                .toList();

        return new PageImpl<>(rows, pageable, productsPage.getTotalElements());
    }

    private StockReportRowResponse toStockRow(ProductEntity product, StockCurrentEntity currentStock) {
        return new StockReportRowResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getUnitOfMeasure(),
                product.isActive(),
                product.isStockControlled(),
                product.getMinimumStock(),
                currentStock == null ? ZERO : currentStock.getCurrentStock(),
                currentStock == null ? null : currentStock.getUpdatedAt()
        );
    }

    private BigDecimal calculateEffectivePurchaseAmount(PurchaseEntity purchase) {
        if (purchase.getStatus() == PurchaseStatus.ANULADA) {
            return ZERO;
        }

        return purchase.getItems().stream()
                .map(item -> item.getUnitCost().multiply(item.getQuantity().subtract(item.getCancelledQuantity())))
                .reduce(ZERO, BigDecimal::add);
    }

    private boolean matchesContext(Long currentContextId, Long requestedContextId) {
        return requestedContextId == null || requestedContextId.equals(currentContextId);
    }

    private boolean matchesDate(LocalDate currentDate, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && currentDate.isBefore(fechaDesde)) {
            return false;
        }
        if (fechaHasta != null && currentDate.isAfter(fechaHasta)) {
            return false;
        }
        return true;
    }

    private boolean matchesDate(LocalDateTime currentDateTime, LocalDate fechaDesde, LocalDate fechaHasta) {
        return matchesDate(currentDateTime.toLocalDate(), fechaDesde, fechaHasta);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? ZERO : value;
    }
}
