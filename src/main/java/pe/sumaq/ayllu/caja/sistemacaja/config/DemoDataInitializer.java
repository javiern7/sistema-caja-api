package pe.sumaq.ayllu.caja.sistemacaja.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.CloseCashBoxUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.OpenCashBoxUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CloseCashBoxRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.OpenCashBoxRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.CreatePurchaseUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CreatePurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application.CreateExpenseUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.domain.ExpenseType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.CreateExpenseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.CreateOperationalContextUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.CreateOperationalContextRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.CreateProductUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.CreateProductRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application.CreateProviderUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.CreateProviderRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application.ReportsQueryService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application.CreateSaleUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.CreateSaleRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SalePaymentRequest;

@Configuration
@Profile("demo")
public class DemoDataInitializer {

    private static final String DEMO_CONTEXT_CODE = "DEMO-NEG-001";
    private static final String DEMO_PROVIDER_DOCUMENT = "20999999991";
    private static final String DEMO_PRODUCT_CAFE_CODE = "DEMO-PROD-CAFE";
    private static final String DEMO_PRODUCT_SANDWICH_CODE = "DEMO-PROD-SAND";

    @Bean
    @Order(20)
    @ConditionalOnProperty(prefix = "app.demo.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
    ApplicationRunner seedDemoData(
            JpaOperationalContextRepository operationalContextRepository,
            JpaProductRepository productRepository,
            JpaProviderRepository providerRepository,
            JpaUserRepository userRepository,
            CreateOperationalContextUseCase createOperationalContextUseCase,
            CreateProductUseCase createProductUseCase,
            CreateProviderUseCase createProviderUseCase,
            CreatePurchaseUseCase createPurchaseUseCase,
            OpenCashBoxUseCase openCashBoxUseCase,
            CreateSaleUseCase createSaleUseCase,
            CreateExpenseUseCase createExpenseUseCase,
            CloseCashBoxUseCase closeCashBoxUseCase,
            ReportsQueryService reportsQueryService
    ) {
        return args -> {
            if (operationalContextRepository.findByCode(DEMO_CONTEXT_CODE).isPresent()) {
                return;
            }

            UserEntity adminUser = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new IllegalStateException("No se encontro el usuario admin para la carga demo."));
            UserEntity cashierUser = userRepository.findByUsername("cajero")
                    .orElseThrow(() -> new IllegalStateException("No se encontro el usuario cajero para la carga demo."));

            SecurityUserPrincipal adminPrincipal = SecurityUserPrincipal.fromEntity(adminUser);
            SecurityUserPrincipal cashierPrincipal = SecurityUserPrincipal.fromEntity(cashierUser);

            OperationalContext operationalContext = createOperationalContextUseCase.execute(
                    new CreateOperationalContextRequest(
                            DEMO_CONTEXT_CODE,
                            "Negocio Demo Central",
                            OperationalContextType.NEGOCIO,
                            OperationalContextStatus.EN_CURSO,
                            LocalDate.now().minusDays(2),
                            null,
                            "Contexto demo para Swagger, Postman e IntelliJ HTTP Client"
                    )
            );

            ProductEntity coffeeProduct = productRepository.findByCode(DEMO_PRODUCT_CAFE_CODE)
                    .orElseGet(() -> createProductUseCase.execute(new CreateProductRequest(
                            DEMO_PRODUCT_CAFE_CODE,
                            "Cafe pasado 12 oz",
                            "UND",
                            new BigDecimal("4.50"),
                            new BigDecimal("2.40"),
                            new BigDecimal("8.00"),
                            true,
                            true,
                            "Producto demo de alta rotacion"
                    )));

            ProductEntity sandwichProduct = productRepository.findByCode(DEMO_PRODUCT_SANDWICH_CODE)
                    .orElseGet(() -> createProductUseCase.execute(new CreateProductRequest(
                            DEMO_PRODUCT_SANDWICH_CODE,
                            "Sandwich mixto",
                            "UND",
                            new BigDecimal("8.00"),
                            new BigDecimal("4.80"),
                            new BigDecimal("5.00"),
                            true,
                            true,
                            "Producto demo de ticket medio"
                    )));

            ProviderEntity providerEntity = providerRepository.findByDocumentNumber(DEMO_PROVIDER_DOCUMENT)
                    .orElseGet(() -> createProviderUseCase.execute(new CreateProviderRequest(
                            "Distribuidora Demo SAC",
                            DEMO_PROVIDER_DOCUMENT,
                            "Rocio Alvarado",
                            "987654321",
                            "demo.proveedor@sistemacaja.test",
                            true
                    )));

            createPurchaseUseCase.execute(
                    adminPrincipal,
                    new CreatePurchaseRequest(
                            operationalContext.id(),
                            providerEntity.getId(),
                            LocalDate.now().minusDays(1),
                            "FACTURA",
                            "F001-000321",
                            "TRANSFERENCIA",
                            List.of(
                                    new PurchaseItemRequest(coffeeProduct.getId(), new BigDecimal("30.00"), new BigDecimal("2.40")),
                                    new PurchaseItemRequest(sandwichProduct.getId(), new BigDecimal("20.00"), new BigDecimal("4.80"))
                            ),
                            "Carga demo inicial de inventario"
                    )
            );

            CashBoxDetailResponse cashBox = openCashBoxUseCase.execute(
                    cashierPrincipal,
                    new OpenCashBoxRequest(
                            operationalContext.id(),
                            new BigDecimal("200.00"),
                            "Apertura automatica demo"
                    )
            );

            createSaleUseCase.execute(
                    cashierPrincipal,
                    new CreateSaleRequest(
                            operationalContext.id(),
                            cashBox.id(),
                            List.of(
                                    new SaleItemRequest(coffeeProduct.getId(), new BigDecimal("4.00"), new BigDecimal("4.50")),
                                    new SaleItemRequest(sandwichProduct.getId(), new BigDecimal("2.00"), new BigDecimal("8.00"))
                            ),
                            List.of(
                                    new SalePaymentRequest("EFECTIVO", new BigDecimal("20.00")),
                                    new SalePaymentRequest("YAPE", new BigDecimal("14.00"))
                            ),
                            "Venta demo mostrador"
                    )
            );

            createExpenseUseCase.execute(
                    cashierPrincipal,
                    new CreateExpenseRequest(
                            operationalContext.id(),
                            cashBox.id(),
                            ExpenseType.CAJA,
                            "MOVILIDAD",
                            "Movilidad para reposicion ligera",
                            "EFECTIVO",
                            new BigDecimal("12.00"),
                            "Caja demo",
                            "Salida de caja demo",
                            LocalDate.now()
                    )
            );

            closeCashBoxUseCase.execute(
                    cashBox.id(),
                    cashierPrincipal,
                    new CloseCashBoxRequest(
                            new BigDecimal("222.00"),
                            "Cierre automatico demo sin diferencias"
                    )
            );

            reportsQueryService.getSalesReport(null, null, operationalContext.id(), adminUser.getUsername(), ReportFormat.JSON);
            reportsQueryService.getCashReport(null, null, operationalContext.id(), adminUser.getUsername(), ReportFormat.JSON);
            reportsQueryService.getPurchasesReport(null, null, operationalContext.id(), adminUser.getUsername(), ReportFormat.JSON);
            reportsQueryService.getExpensesReport(null, null, operationalContext.id(), adminUser.getUsername(), ReportFormat.JSON);
            reportsQueryService.getStockReport(null, null, operationalContext.id(), adminUser.getUsername(), ReportFormat.JSON);
            reportsQueryService.getUtilityReport(null, null, operationalContext.id(), adminUser.getUsername(), ReportFormat.JSON);
        };
    }
}
