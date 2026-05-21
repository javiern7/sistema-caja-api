package pe.sumaq.ayllu.caja.sistemacaja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.AuditOperationEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.JpaAuditOperationRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.PurchaseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.domain.ExpenseType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.ExpenseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.JpaReportHistoryRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.ReportHistoryEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.PermissionEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaPermissionRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.SaleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "app.security.seed.enabled=false"
})
@AutoConfigureMockMvc
class PaginationEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaOperationalContextRepository jpaOperationalContextRepository;

    @MockBean
    private JpaUserRepository jpaUserRepository;

    @MockBean
    private JpaRoleRepository jpaRoleRepository;

    @MockBean
    private JpaPermissionRepository jpaPermissionRepository;

    @MockBean
    private JpaProductRepository jpaProductRepository;

    @MockBean
    private JpaProviderRepository jpaProviderRepository;

    @MockBean
    private JpaStockCurrentRepository jpaStockCurrentRepository;

    @MockBean
    private JpaStockMovementRepository jpaStockMovementRepository;

    @MockBean
    private JpaCashBoxRepository jpaCashBoxRepository;

    @MockBean
    private JpaCashMovementRepository jpaCashMovementRepository;

    @MockBean
    private JpaSaleRepository jpaSaleRepository;

    @MockBean
    private JpaPurchaseRepository jpaPurchaseRepository;

    @MockBean
    private JpaExpenseRepository jpaExpenseRepository;

    @MockBean
    private JpaAuditOperationRepository jpaAuditOperationRepository;

    @MockBean
    private JpaReportHistoryRepository jpaReportHistoryRepository;

    @Test
    void auditOperationsEndpointShouldReturnPaginatedResponse() throws Exception {
        AuditOperationEntity entity = new AuditOperationEntity();
        entity.setId(10L);
        entity.setModule("VENTAS");
        entity.setOperationType("CREAR");
        entity.setEntityType("SALE");
        entity.setEntityId("100");
        entity.setUsername("admin");
        entity.setOccurredAt(LocalDateTime.of(2026, 5, 20, 10, 30));
        entity.setDetail("Operacion de prueba");

        when(jpaAuditOperationRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(entity),
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "occurredAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/auditoria/operaciones")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("auditoria.consultar")))
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "occurredAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(10))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.sort[0]").value("occurredAt,desc"));
    }

    @Test
    void reportHistoryEndpointShouldReturnPaginatedResponse() throws Exception {
        ReportHistoryEntity entity = new ReportHistoryEntity();
        entity.setId(21L);
        entity.setReportType("VENTAS");
        entity.setFormat("XLSX");
        entity.setGeneratedBy("admin");
        entity.setFilters("fechaDesde=2026-05-01");
        entity.setGeneratedAt(LocalDateTime.of(2026, 5, 20, 11, 15));

        when(jpaReportHistoryRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(entity),
                        PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "generatedAt")),
                        11
                ));

        mockMvc.perform(get("/api/v1/reportes/historial")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("reporte.ver")))
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "generatedAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(21))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(11))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.sort[0]").value("generatedAt,desc"));
    }

    @Test
    void auditOperationsEndpointShouldRejectInvalidSortField() throws Exception {
        mockMvc.perform(get("/api/v1/auditoria/operaciones")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("auditoria.consultar")))
                        .param("sort", "detailHack,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void expensesEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity operationalContext = new OperationalContextEntity();
        operationalContext.setId(3L);
        operationalContext.setName("Negocio Centro");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("admin");

        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setId(7L);
        expenseEntity.setOperationalContext(operationalContext);
        expenseEntity.setRecordedBy(userEntity);
        expenseEntity.setExpenseType(ExpenseType.ADMINISTRATIVO);
        expenseEntity.setCategory("Servicios");
        expenseEntity.setDescription("Internet");
        expenseEntity.setPaymentMethod("TRANSFERENCIA");
        expenseEntity.setAmount(new BigDecimal("120.00"));
        expenseEntity.setResponsible("Caja central");
        expenseEntity.setObservation("Pago mensual");
        expenseEntity.setExpenseDate(LocalDate.of(2026, 5, 20));
        expenseEntity.setCreatedAt(LocalDateTime.of(2026, 5, 20, 12, 0));

        when(jpaExpenseRepository.findAllByOrderByCreatedAtDesc(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(expenseEntity),
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/egresos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("egreso.registrar")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(7))
                .andExpect(jsonPath("$.data.items[0].category").value("Servicios"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void productsEndpointShouldReturnPaginatedResponse() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(14L);
        productEntity.setCode("PROD-001");
        productEntity.setName("Cafe filtrado");
        productEntity.setUnitOfMeasure("UND");
        productEntity.setSalePrice(new BigDecimal("8.50"));
        productEntity.setReferenceCost(new BigDecimal("4.20"));
        productEntity.setMinimumStock(new BigDecimal("5.00"));
        productEntity.setStockControlled(true);
        productEntity.setActive(true);
        productEntity.setDescription("Producto de prueba");

        when(jpaProductRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(productEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "name")),
                        1
                ));

        mockMvc.perform(get("/api/v1/productos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("producto.gestionar")))
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].code").value("PROD-001"))
                .andExpect(jsonPath("$.data.items[0].name").value("Cafe filtrado"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void currentStockEndpointShouldReturnPaginatedResponse() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(15L);
        productEntity.setCode("PROD-015");
        productEntity.setName("Azucar rubia");
        productEntity.setUnitOfMeasure("KG");
        productEntity.setMinimumStock(new BigDecimal("3.00"));
        productEntity.setStockControlled(true);
        productEntity.setActive(true);

        StockCurrentEntity stockCurrentEntity = new StockCurrentEntity();
        stockCurrentEntity.setProductId(15L);
        stockCurrentEntity.setProduct(productEntity);
        stockCurrentEntity.setCurrentStock(new BigDecimal("12.50"));
        stockCurrentEntity.setUpdatedAt(LocalDateTime.of(2026, 5, 20, 9, 0));

        when(jpaProductRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(productEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "name")),
                        1
                ));
        when(jpaStockCurrentRepository.findAllByProductIdIn(any()))
                .thenReturn(List.of(stockCurrentEntity));

        mockMvc.perform(get("/api/v1/stock")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("stock.consultar"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].productCode").value("PROD-015"))
                .andExpect(jsonPath("$.data.items[0].currentStock").value(12.50))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void stockMovementsEndpointShouldReturnPaginatedResponse() throws Exception {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(9L);
        productEntity.setCode("INS-001");
        productEntity.setName("Insumo");

        StockMovementEntity movementEntity = new StockMovementEntity();
        movementEntity.setId(31L);
        movementEntity.setProduct(productEntity);
        movementEntity.setMovementType(pe.sumaq.ayllu.caja.sistemacaja.modules.stock.domain.StockMovementType.ENTRADA);
        movementEntity.setQuantity(new BigDecimal("12.00"));
        movementEntity.setReferenceType("COMPRA");
        movementEntity.setReferenceId("100");
        movementEntity.setPerformedBy("admin");
        movementEntity.setOccurredAt(LocalDateTime.of(2026, 5, 20, 13, 0));
        movementEntity.setNote("Ingreso");

        when(jpaStockMovementRepository.findAllByOrderByOccurredAtDesc(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(movementEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "occurredAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/stock/movimientos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("stock.consultar"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].id").value(31))
                .andExpect(jsonPath("$.data.items[0].productCode").value("INS-001"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void salesEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity operationalContext = new OperationalContextEntity();
        operationalContext.setId(6L);
        operationalContext.setName("Sucursal Norte");

        UserEntity soldBy = new UserEntity();
        soldBy.setUsername("cajero1");

        CashBoxEntity cashBoxEntity = new CashBoxEntity();
        cashBoxEntity.setId(13L);

        SaleEntity saleEntity = new SaleEntity();
        saleEntity.setId(41L);
        saleEntity.setOperationalContext(operationalContext);
        saleEntity.setCashBox(cashBoxEntity);
        saleEntity.setSoldBy(soldBy);
        saleEntity.setStatus(SaleStatus.REGISTRADA);
        saleEntity.setSubtotalAmount(new BigDecimal("25.00"));
        saleEntity.setTotalAmount(new BigDecimal("30.00"));
        saleEntity.setInternalReceiptSeries("B001");
        saleEntity.setInternalReceiptNumber(55L);
        saleEntity.setObservation("Venta mostrador");
        saleEntity.setCreatedAt(LocalDateTime.of(2026, 5, 20, 14, 0));

        when(jpaSaleRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(saleEntity),
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/ventas")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("venta.registrar")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(41))
                .andExpect(jsonPath("$.data.items[0].soldByUsername").value("cajero1"))
                .andExpect(jsonPath("$.data.items[0].itemsCount").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void purchasesEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity operationalContext = new OperationalContextEntity();
        operationalContext.setId(7L);
        operationalContext.setName("Sucursal Sur");

        ProviderEntity providerEntity = new ProviderEntity();
        providerEntity.setId(5L);
        providerEntity.setName("Proveedor Demo");

        UserEntity purchasedBy = new UserEntity();
        purchasedBy.setUsername("comprador1");

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(51L);
        purchaseEntity.setOperationalContext(operationalContext);
        purchaseEntity.setProvider(providerEntity);
        purchaseEntity.setPurchasedBy(purchasedBy);
        purchaseEntity.setStatus(PurchaseStatus.REGISTRADA);
        purchaseEntity.setPurchaseDate(LocalDate.of(2026, 5, 20));
        purchaseEntity.setDocumentType("FACTURA");
        purchaseEntity.setDocumentNumber("F001-99");
        purchaseEntity.setPaymentMethod("EFECTIVO");
        purchaseEntity.setSubtotalAmount(new BigDecimal("80.00"));
        purchaseEntity.setTotalAmount(new BigDecimal("94.40"));
        purchaseEntity.setObservation("Compra de prueba");
        purchaseEntity.setCreatedAt(LocalDateTime.of(2026, 5, 20, 15, 0));

        when(jpaPurchaseRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(purchaseEntity),
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/compras")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("compra.registrar")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(51))
                .andExpect(jsonPath("$.data.items[0].providerName").value("Proveedor Demo"))
                .andExpect(jsonPath("$.data.items[0].itemsCount").value(0))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void cashBoxesEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity operationalContext = new OperationalContextEntity();
        operationalContext.setId(8L);
        operationalContext.setCode("CTX-008");
        operationalContext.setName("Caja Principal");

        UserEntity openedBy = new UserEntity();
        openedBy.setId(3L);
        openedBy.setUsername("cajero-central");

        CashBoxEntity cashBoxEntity = new CashBoxEntity();
        cashBoxEntity.setId(61L);
        cashBoxEntity.setOperationalContext(operationalContext);
        cashBoxEntity.setOpenedBy(openedBy);
        cashBoxEntity.setStatus(CashBoxStatus.ABIERTA);
        cashBoxEntity.setOpeningAmount(new BigDecimal("100.00"));
        cashBoxEntity.setExpectedAmount(new BigDecimal("150.00"));
        cashBoxEntity.setOpeningObservation("Turno mañana");
        cashBoxEntity.setOpenedAt(LocalDateTime.of(2026, 5, 20, 8, 0));

        when(jpaCashBoxRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(cashBoxEntity),
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "openedAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/cajas")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("caja.abrir")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].id").value(61))
                .andExpect(jsonPath("$.data.items[0].operationalContextCode").value("CTX-008"))
                .andExpect(jsonPath("$.data.items[0].openedByUsername").value("cajero-central"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void salesReportDetailEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity operationalContext = new OperationalContextEntity();
        operationalContext.setId(22L);
        operationalContext.setName("Local Centro");

        UserEntity soldBy = new UserEntity();
        soldBy.setUsername("vendedor");

        CashBoxEntity cashBoxEntity = new CashBoxEntity();
        cashBoxEntity.setId(2L);

        SaleEntity saleEntity = new SaleEntity();
        saleEntity.setId(101L);
        saleEntity.setOperationalContext(operationalContext);
        saleEntity.setCashBox(cashBoxEntity);
        saleEntity.setSoldBy(soldBy);
        saleEntity.setStatus(SaleStatus.REGISTRADA);
        saleEntity.setSubtotalAmount(new BigDecimal("18.00"));
        saleEntity.setTotalAmount(new BigDecimal("20.00"));
        saleEntity.setInternalReceiptSeries("B001");
        saleEntity.setInternalReceiptNumber(123L);
        saleEntity.setCreatedAt(LocalDateTime.of(2026, 5, 20, 10, 0));

        when(jpaSaleRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(saleEntity),
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/reportes/ventas/detalle")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("reporte.ventas"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].saleId").value(101))
                .andExpect(jsonPath("$.data.items[0].internalReceipt").value("B001-123"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void cashReportDetailEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity operationalContext = new OperationalContextEntity();
        operationalContext.setId(23L);
        operationalContext.setName("Evento Norte");

        UserEntity openedBy = new UserEntity();
        openedBy.setUsername("apertura");

        CashBoxEntity cashBoxEntity = new CashBoxEntity();
        cashBoxEntity.setId(111L);
        cashBoxEntity.setOperationalContext(operationalContext);
        cashBoxEntity.setOpenedBy(openedBy);
        cashBoxEntity.setStatus(CashBoxStatus.CERRADA);
        cashBoxEntity.setOpeningAmount(new BigDecimal("80.00"));
        cashBoxEntity.setExpectedAmount(new BigDecimal("120.00"));
        cashBoxEntity.setCountedAmount(new BigDecimal("118.00"));
        cashBoxEntity.setDifferenceAmount(new BigDecimal("-2.00"));
        cashBoxEntity.setOpenedAt(LocalDateTime.of(2026, 5, 20, 8, 0));
        cashBoxEntity.setClosedAt(LocalDateTime.of(2026, 5, 20, 18, 0));

        when(jpaCashBoxRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(cashBoxEntity),
                        PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "openedAt")),
                        1
                ));

        mockMvc.perform(get("/api/v1/reportes/caja/detalle")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("reporte.caja"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].cashBoxId").value(111))
                .andExpect(jsonPath("$.data.items[0].status").value("CERRADA"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void providersEndpointShouldReturnPaginatedResponse() throws Exception {
        ProviderEntity providerEntity = new ProviderEntity();
        providerEntity.setId(4L);
        providerEntity.setName("Proveedor Norte");
        providerEntity.setDocumentNumber("20111111111");
        providerEntity.setContactName("Marta");
        providerEntity.setPhone("999999999");
        providerEntity.setEmail("proveedor@example.com");
        providerEntity.setActive(true);

        when(jpaProviderRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(providerEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "name")),
                        1
                ));

        mockMvc.perform(get("/api/v1/proveedores")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("proveedor.gestionar"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].name").value("Proveedor Norte"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void usersEndpointShouldReturnPaginatedResponse() throws Exception {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(2L);
        roleEntity.setName("CAJERO");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(8L);
        userEntity.setUsername("cajero");
        userEntity.setActive(true);
        userEntity.setRole(roleEntity);

        when(jpaUserRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(userEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "username")),
                        1
                ));

        mockMvc.perform(get("/api/v1/usuarios")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("usuario.gestionar"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].username").value("cajero"))
                .andExpect(jsonPath("$.data.items[0].roleName").value("CAJERO"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void rolesEndpointShouldReturnPaginatedResponse() throws Exception {
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setCode("venta.registrar");
        permissionEntity.setDescription("Registrar ventas");

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(5L);
        roleEntity.setName("SUPERVISOR");
        roleEntity.setDescription("Rol supervisor");
        roleEntity.setPermissions(new java.util.LinkedHashSet<>(List.of(permissionEntity)));

        when(jpaRoleRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(roleEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "name")),
                        1
                ));

        mockMvc.perform(get("/api/v1/roles")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("rol.gestionar"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].name").value("SUPERVISOR"))
                .andExpect(jsonPath("$.data.items[0].permissions[0].code").value("venta.registrar"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void operationalContextsEndpointShouldReturnPaginatedResponse() throws Exception {
        OperationalContextEntity contextEntity = new OperationalContextEntity();
        contextEntity.setId(12L);
        contextEntity.setCode("CTX-001");
        contextEntity.setName("Sucursal Centro");
        contextEntity.setType(OperationalContextType.NEGOCIO);
        contextEntity.setStatus(OperationalContextStatus.EN_CURSO);
        contextEntity.setStartDate(LocalDate.of(2026, 5, 1));
        contextEntity.setDescription("Contexto activo");

        when(jpaOperationalContextRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(
                        List.of(contextEntity),
                        PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "startDate").and(Sort.by(Sort.Direction.ASC, "name"))),
                        1
                ));

        mockMvc.perform(get("/api/v1/negocios-eventos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("negocioevento.gestionar"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].code").value("CTX-001"))
                .andExpect(jsonPath("$.data.items[0].status").value("EN_CURSO"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}
