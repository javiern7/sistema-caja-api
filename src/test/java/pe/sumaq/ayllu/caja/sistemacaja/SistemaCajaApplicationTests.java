package pe.sumaq.ayllu.caja.sistemacaja;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.JpaAuditOperationRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportExportService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportsQueryService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.JpaReportHistoryRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.SalesReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.StockReportResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaPermissionRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.PermissionEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application.GetSaleDetailUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.GetPurchaseDetailUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application.GetExpenseDetailUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.common.application.OperationalDetailPdfExportService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.application.OperationalDataResetService;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
class SistemaCajaApplicationTests {

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

    @MockBean
    private OperationalDataResetService operationalDataResetService;

    @MockBean
    private ReportsQueryService reportsQueryService;

    @MockBean
    private ReportExportService reportExportService;

    @MockBean
    private OperationalDetailPdfExportService operationalDetailPdfExportService;

    @MockBean
    private GetSaleDetailUseCase getSaleDetailUseCase;

    @MockBean
    private GetPurchaseDetailUseCase getPurchaseDetailUseCase;

    @MockBean
    private GetExpenseDetailUseCase getExpenseDetailUseCase;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointShouldBePublic() throws Exception {
        mockMvc.perform(get("/api/v1/system/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void meEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void operationalContextsEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/contextos-operativos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void stockEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/stock"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void activeCashBoxEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/cajas/activa"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void saleDetailEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void purchaseDetailEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/compras/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void expenseDetailEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/egresos/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void saleDetailPdfEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/ventas/1/exportar-pdf"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void purchaseDetailPdfEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/compras/1/exportar-pdf"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void expenseDetailPdfEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/egresos/1/exportar-pdf"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void auditOperationsEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/auditoria/operaciones"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void reportsEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/reportes/ventas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void operationalDataResetEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/system/operational-data/reset"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void operationalContextsEndpointShouldRejectAuthenticatedUserWithoutOperationalPermission() throws Exception {
        mockMvc.perform(get("/api/v1/contextos-operativos")
                        .with(user("visitante").authorities(new SimpleGrantedAuthority[0])))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN_OPERATION"));
    }

    @Test
    void operationalDataResetEndpointShouldRejectAuthenticatedUserWithoutAdminPermission() throws Exception {
        mockMvc.perform(post("/api/v1/system/operational-data/reset")
                        .with(user("visitante").authorities(new SimpleGrantedAuthority[0])))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN_OPERATION"));
    }

    @Test
    void operationalContextsEndpointShouldAllowReportsUserToResolveReportFilters() throws Exception {
        OperationalContextEntity contextEntity = new OperationalContextEntity();
        contextEntity.setId(10L);
        contextEntity.setCode("CTX-REPORT-001");
        contextEntity.setName("Contexto Reportable");
        contextEntity.setType(OperationalContextType.NEGOCIO);
        contextEntity.setStatus(OperationalContextStatus.EN_CURSO);
        contextEntity.setStartDate(LocalDate.of(2026, 5, 30));

        when(jpaOperationalContextRepository.findAllByStatusOrderByStartDateAsc(OperationalContextStatus.EN_CURSO))
                .thenReturn(List.of(contextEntity));

        mockMvc.perform(get("/api/v1/contextos-operativos")
                        .with(user("reportes").authorities(new SimpleGrantedAuthority("reporte.ver"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].code").value("CTX-REPORT-001"))
                .andExpect(jsonPath("$.data[0].status").value("EN_CURSO"));
    }

    @Test
    void salesEndpointShouldRejectAuthenticatedUserWithoutSalesPermission() throws Exception {
        mockMvc.perform(get("/api/v1/ventas")
                        .with(user("visitante").authorities(new SimpleGrantedAuthority[0])))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN_OPERATION"));
    }

    @Test
    void productsEndpointShouldRejectAuthenticatedUserWithoutCatalogOrOperationalPermission() throws Exception {
        mockMvc.perform(get("/api/v1/productos")
                        .with(user("visitante").authorities(new SimpleGrantedAuthority[0])))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN_OPERATION"));
    }

    @Test
    void loginShouldReturnTokenForAdminFromPersistentUserStore() throws Exception {
        when(jpaUserRepository.findByUsername("admin")).thenReturn(Optional.of(buildAdminUser()));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin123*"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.permissions").isArray());
    }

    @Test
    void createUserShouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
        when(jpaUserRepository.existsByUsername("duplicado")).thenReturn(true);

        mockMvc.perform(post("/api/v1/usuarios")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("usuario.gestionar")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "duplicado",
                                  "password": "Admin123*",
                                  "roleId": 1,
                                  "active": true
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void createProductShouldReturnConflictWhenCodeAlreadyExists() throws Exception {
        when(jpaProductRepository.existsByCode("PROD-EXISTE")).thenReturn(true);

        mockMvc.perform(post("/api/v1/productos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("producto.gestionar")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "PROD-EXISTE",
                                  "name": "Producto repetido",
                                  "unitOfMeasure": "UND",
                                  "salePrice": 10.00,
                                  "referenceCost": 5.00,
                                  "minimumStock": 1.00,
                                  "stockControlled": true,
                                  "active": true,
                                  "description": "Duplicado de prueba"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void salesExportEndpointShouldReturnExcelBinaryContract() throws Exception {
        when(reportsQueryService.getSalesReport(null, null, null, "reportes", pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat.XLSX))
                .thenReturn(new SalesReportResponse(null, null, null, 0, java.math.BigDecimal.ZERO, List.of()));
        when(reportExportService.exportSalesToExcel(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/v1/reportes/ventas/exportar")
                        .with(user(SecurityUserPrincipal.authenticated(10L, "reportes", "REPORTES", true, List.of("reporte.exportar"))))
                        .param("formato", "xlsx"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("reporte-ventas.xlsx")));
    }

    @Test
    void stockExportEndpointShouldRequireOperationalContextAndReturnPdfContract() throws Exception {
        when(reportsQueryService.getStockReport(null, null, 10L, "reportes", pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat.PDF))
                .thenReturn(new StockReportResponse("OPERATIONAL_CONTEXT", 10L, true, 0, java.math.BigDecimal.ZERO, List.of()));
        when(reportExportService.exportStockToPdf(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new byte[]{4, 5, 6});

        mockMvc.perform(get("/api/v1/reportes/stock/exportar")
                        .with(user(SecurityUserPrincipal.authenticated(10L, "reportes", "REPORTES", true, List.of("reporte.exportar"))))
                        .param("formato", "pdf")
                        .param("operationalContextId", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("reporte-stock.pdf")));
    }

    @Test
    void saleDetailPdfEndpointShouldReturnPdfBinaryContract() throws Exception {
        SaleResponse sale = new SaleResponse(
                15L, 10L, "Contexto operativo", 20L, "cajero", null,
                java.math.BigDecimal.TEN, java.math.BigDecimal.TEN, "B001", 25L,
                null, null, null, null, null, List.of(), List.of()
        );
        when(getSaleDetailUseCase.execute(15L)).thenReturn(sale);
        when(operationalDetailPdfExportService.saleFileName(sale)).thenReturn("venta-B001-25.pdf");
        when(operationalDetailPdfExportService.exportSaleDetailToPdf(sale)).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/v1/ventas/15/exportar-pdf")
                        .with(user(SecurityUserPrincipal.authenticated(10L, "ventas", "VENTAS", true, List.of("venta.registrar")))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("venta-B001-25.pdf")));
    }

    @Test
    void purchaseDetailPdfEndpointShouldReturnPdfBinaryContract() throws Exception {
        PurchaseResponse purchase = new PurchaseResponse(
                18L, 10L, "Contexto operativo", 7L, "Proveedor Demo", null,
                LocalDate.of(2026, 6, 12), "FACTURA", "F001-88", "TRANSFERENCIA",
                java.math.BigDecimal.TEN, java.math.BigDecimal.TEN, null, null, null, null, null, List.of()
        );
        when(getPurchaseDetailUseCase.execute(18L)).thenReturn(purchase);
        when(operationalDetailPdfExportService.purchaseFileName(purchase)).thenReturn("compra-F001-88.pdf");
        when(operationalDetailPdfExportService.exportPurchaseDetailToPdf(purchase)).thenReturn(new byte[]{4, 5, 6});

        mockMvc.perform(get("/api/v1/compras/18/exportar-pdf")
                        .with(user(SecurityUserPrincipal.authenticated(10L, "compras", "COMPRAS", true, List.of("compra.registrar")))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("compra-F001-88.pdf")));
    }

    @Test
    void expenseDetailPdfEndpointShouldReturnPdfBinaryContract() throws Exception {
        ExpenseResponse expense = new ExpenseResponse(
                22L, 10L, "Contexto operativo", 5L, null, "Movilidad", "Taxi aeropuerto",
                "EFECTIVO", java.math.BigDecimal.TEN, "Operador", null, "tesoreria",
                LocalDate.of(2026, 6, 12), null
        );
        when(getExpenseDetailUseCase.execute(22L)).thenReturn(expense);
        when(operationalDetailPdfExportService.expenseFileName(expense)).thenReturn("egreso-id-22.pdf");
        when(operationalDetailPdfExportService.exportExpenseDetailToPdf(expense)).thenReturn(new byte[]{7, 8, 9});

        mockMvc.perform(get("/api/v1/egresos/22/exportar-pdf")
                        .with(user(SecurityUserPrincipal.authenticated(10L, "egresos", "EGRESOS", true, List.of("egreso.registrar")))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("egreso-id-22.pdf")));
    }

    @Test
    void utilityExportEndpointShouldRejectUnsupportedFormat() throws Exception {
        mockMvc.perform(get("/api/v1/reportes/utilidad/exportar")
                        .with(user(SecurityUserPrincipal.authenticated(10L, "reportes", "REPORTES", true, List.of("reporte.exportar"))))
                        .param("formato", "csv"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.details[0]").value("allowedFormats=xlsx,pdf"));
    }

    private UserEntity buildAdminUser() {
        PermissionEntity usersPermission = new PermissionEntity();
        usersPermission.setCode("usuario.gestionar");
        usersPermission.setDescription("Gestion de usuarios");

        PermissionEntity rolesPermission = new PermissionEntity();
        rolesPermission.setCode("rol.gestionar");
        rolesPermission.setDescription("Gestion de roles");

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1L);
        roleEntity.setName("ADMIN");
        roleEntity.setDescription("Administrador");
        roleEntity.setPermissions(new LinkedHashSet<>(List.of(usersPermission, rolesPermission)));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("admin");
        userEntity.setPasswordHash(new BCryptPasswordEncoder().encode("Admin123*"));
        userEntity.setActive(true);
        userEntity.setRole(roleEntity);
        return userEntity;
    }
}
