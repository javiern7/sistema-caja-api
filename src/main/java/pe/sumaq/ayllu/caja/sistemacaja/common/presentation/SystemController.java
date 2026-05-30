package pe.sumaq.ayllu.caja.sistemacaja.common.presentation;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.application.OperationalDataResetService;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "Sistema", description = "Endpoints tecnicos y de diagnostico")
public class SystemController {

    private final ApiResponseFactory responseFactory;
    private final Environment environment;
    private final Optional<OperationalDataResetService> operationalDataResetService;

    public SystemController(
            ApiResponseFactory responseFactory,
            Environment environment,
            Optional<OperationalDataResetService> operationalDataResetService
    ) {
        this.responseFactory = responseFactory;
        this.environment = environment;
        this.operationalDataResetService = operationalDataResetService;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica disponibilidad del servicio y perfil activo.")
    public ApiResponse<Map<String, Object>> health() {
        return responseFactory.success("Servicio disponible.", Map.of(
                "status", "UP",
                "application", environment.getProperty("spring.application.name", "sistema-caja-api"),
                "profiles", Arrays.asList(environment.getActiveProfiles()),
                "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @PostMapping("/operational-data/reset")
    @PreAuthorize("hasAuthority('usuario.gestionar')")
    @Operation(summary = "Reiniciar datos operativos", description = "Limpia datos operativos y reejecuta la semilla activa en perfiles locales, qa o demo.")
    public ApiResponse<OperationalDataResetService.ResetResult> resetOperationalData() {
        OperationalDataResetService resetService = operationalDataResetService.orElseThrow(() -> new BusinessException(
                ErrorCode.FORBIDDEN_OPERATION,
                HttpStatus.FORBIDDEN,
                "El reinicio operativo no se encuentra habilitado en el entorno actual."
        ));

        return responseFactory.success(
                "Datos operativos reiniciados correctamente.",
                resetService.resetAndReseed()
        );
    }
}
