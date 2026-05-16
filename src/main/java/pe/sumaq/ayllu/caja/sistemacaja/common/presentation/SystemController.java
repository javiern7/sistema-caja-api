package pe.sumaq.ayllu.caja.sistemacaja.common.presentation;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;

@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    private final ApiResponseFactory responseFactory;
    private final Environment environment;

    public SystemController(ApiResponseFactory responseFactory, Environment environment) {
        this.responseFactory = responseFactory;
        this.environment = environment;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return responseFactory.success("Servicio disponible.", Map.of(
                "status", "UP",
                "application", environment.getProperty("spring.application.name", "sistema-caja-api"),
                "profiles", Arrays.asList(environment.getActiveProfiles()),
                "timestamp", OffsetDateTime.now().toString()
        ));
    }
}
