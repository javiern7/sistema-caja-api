package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation;

import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.application.AuthenticateUserUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.application.GetAuthenticatedUserUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain.AuthenticatedUser;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto.LoginRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto.LoginResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
    private final ApiResponseFactory responseFactory;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase,
            GetAuthenticatedUserUseCase getAuthenticatedUserUseCase,
            ApiResponseFactory responseFactory
    ) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.getAuthenticatedUserUseCase = getAuthenticatedUserUseCase;
        this.responseFactory = responseFactory;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return responseFactory.success(
                "Login correcto.",
                authenticateUserUseCase.execute(request)
        );
    }

    @GetMapping("/me")
    public ApiResponse<AuthenticatedUser> me(Authentication authentication) {
        return responseFactory.success(
                "Sesion recuperada correctamente.",
                getAuthenticatedUserUseCase.execute(authentication)
        );
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Object>> logout() {
        return responseFactory.success(
                "Logout registrado correctamente.",
                Map.of("stateless", true)
        );
    }
}
