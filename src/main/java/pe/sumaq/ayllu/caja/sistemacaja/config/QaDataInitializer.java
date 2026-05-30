package pe.sumaq.ayllu.caja.sistemacaja.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.OpenCashBoxUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.OpenCashBoxRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.CreatePurchaseUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CreatePurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseItemRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.CreateOperationalContextUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application.CreateProductUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.CreateProductRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application.CreateProviderUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.CreateProviderRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Configuration
@Profile("qa")
public class QaDataInitializer {

    private static final String QA_CONTEXT_CODE = "QA-BASE-001";
    private static final String QA_PROVIDER_DOCUMENT = "20990000001";
    private static final String QA_PURCHASE_DOCUMENT = "QA-SEED-COMPRA-001";
    private static final String QA_PRODUCT_CODE = "PROD-QA-001";

    @Bean
    @Order(20)
    @ConditionalOnProperty(prefix = "app.qa.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
    ApplicationRunner seedQaData(
            JpaOperationalContextRepository operationalContextRepository,
            JpaProductRepository productRepository,
            JpaProviderRepository providerRepository,
            JpaPurchaseRepository purchaseRepository,
            JpaCashBoxRepository cashBoxRepository,
            JpaUserRepository userRepository,
            CreateOperationalContextUseCase createOperationalContextUseCase,
            CreateProductUseCase createProductUseCase,
            CreateProviderUseCase createProviderUseCase,
            CreatePurchaseUseCase createPurchaseUseCase,
            OpenCashBoxUseCase openCashBoxUseCase,
            @Value("${app.qa.seed.open-cash:false}") boolean openCash
    ) {
        return args -> {
            UserEntity adminUser = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new IllegalStateException("No se encontro el usuario admin para la carga QA."));
            UserEntity cashierUser = userRepository.findByUsername("cajero")
                    .orElseThrow(() -> new IllegalStateException("No se encontro el usuario cajero para la carga QA."));

            SecurityUserPrincipal adminPrincipal = SecurityUserPrincipal.fromEntity(adminUser);
            SecurityUserPrincipal cashierPrincipal = SecurityUserPrincipal.fromEntity(cashierUser);

            OperationalContext operationalContext = operationalContextRepository.findByCode(QA_CONTEXT_CODE)
                    .map(entity -> new OperationalContext(
                            entity.getId(),
                            entity.getCode(),
                            entity.getName(),
                            entity.getType(),
                            entity.getStatus(),
                            entity.getStartDate(),
                            entity.getEndDate(),
                            entity.getDescription()
                    ))
                    .orElseGet(() -> createOperationalContextUseCase.execute(
                            new pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.CreateOperationalContextRequest(
                                    QA_CONTEXT_CODE,
                                    "Contexto QA Base",
                                    OperationalContextType.NEGOCIO,
                                    OperationalContextStatus.EN_CURSO,
                                    LocalDate.now(),
                                    null,
                                    "Contexto neutro para QA repetible del MVP"
                            )
                    ));

            ProductEntity productEntity = productRepository.findByCode(QA_PRODUCT_CODE)
                    .orElseGet(() -> createProductUseCase.execute(new CreateProductRequest(
                            QA_PRODUCT_CODE,
                            "Producto QA Base",
                            "UND",
                            new BigDecimal("15.00"),
                            new BigDecimal("8.00"),
                            new BigDecimal("2.00"),
                            true,
                            true,
                            "Producto minimo para flujo QA"
                    )));

            ProviderEntity providerEntity = providerRepository.findByDocumentNumber(QA_PROVIDER_DOCUMENT)
                    .orElseGet(() -> createProviderUseCase.execute(new CreateProviderRequest(
                            "Proveedor QA Base",
                            QA_PROVIDER_DOCUMENT,
                            "Contacto QA",
                            "999888777",
                            "qa.proveedor@sistemacaja.test",
                            true
                    )));

            if (purchaseRepository.findByDocumentNumber(QA_PURCHASE_DOCUMENT).isEmpty()) {
                createPurchaseUseCase.execute(
                        adminPrincipal,
                        new CreatePurchaseRequest(
                                operationalContext.id(),
                                providerEntity.getId(),
                                LocalDate.now(),
                                "INTERNO",
                                QA_PURCHASE_DOCUMENT,
                                "EFECTIVO",
                                List.of(new PurchaseItemRequest(productEntity.getId(), new BigDecimal("10.00"), new BigDecimal("8.00"))),
                                "Compra semilla QA para poblar stock"
                        )
                );
            }

            if (openCash && !cashBoxRepository.existsByOperationalContextIdAndStatus(operationalContext.id(), CashBoxStatus.ABIERTA)) {
                openCashBoxUseCase.execute(
                        cashierPrincipal,
                        new OpenCashBoxRequest(
                                operationalContext.id(),
                                new BigDecimal("100.00"),
                                "Apertura opcional semilla QA"
                        )
                );
            }
        };
    }
}
