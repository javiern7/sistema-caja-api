package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.CloseCashBoxUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.GetActiveCashBoxUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.GetCashBoxSummaryUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application.OpenCashBoxUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CloseCashBoxRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.OpenCashBoxRequest;

@RestController
@RequestMapping("/api/v1/cajas")
public class CashBoxesController {

    private final OpenCashBoxUseCase openCashBoxUseCase;
    private final GetActiveCashBoxUseCase getActiveCashBoxUseCase;
    private final GetCashBoxSummaryUseCase getCashBoxSummaryUseCase;
    private final CloseCashBoxUseCase closeCashBoxUseCase;
    private final ApiResponseFactory responseFactory;

    public CashBoxesController(
            OpenCashBoxUseCase openCashBoxUseCase,
            GetActiveCashBoxUseCase getActiveCashBoxUseCase,
            GetCashBoxSummaryUseCase getCashBoxSummaryUseCase,
            CloseCashBoxUseCase closeCashBoxUseCase,
            ApiResponseFactory responseFactory
    ) {
        this.openCashBoxUseCase = openCashBoxUseCase;
        this.getActiveCashBoxUseCase = getActiveCashBoxUseCase;
        this.getCashBoxSummaryUseCase = getCashBoxSummaryUseCase;
        this.closeCashBoxUseCase = closeCashBoxUseCase;
        this.responseFactory = responseFactory;
    }

    @PostMapping("/aperturas")
    @PreAuthorize("hasAuthority('caja.abrir')")
    public ApiResponse<CashBoxDetailResponse> openCashBox(
            Authentication authentication,
            @Valid @RequestBody OpenCashBoxRequest request
    ) {
        return responseFactory.success(
                "Caja abierta correctamente.",
                openCashBoxUseCase.execute(extractPrincipal(authentication), request)
        );
    }

    @GetMapping("/activa")
    public ApiResponse<CashBoxDetailResponse> getActiveCashBox(Authentication authentication) {
        return responseFactory.success(
                "Caja activa obtenida correctamente.",
                getActiveCashBoxUseCase.execute(extractPrincipal(authentication))
        );
    }

    @GetMapping("/{cashBoxId}/resumen")
    public ApiResponse<CashBoxDetailResponse> getCashBoxSummary(@PathVariable Long cashBoxId) {
        return responseFactory.success(
                "Resumen de caja obtenido correctamente.",
                getCashBoxSummaryUseCase.execute(cashBoxId)
        );
    }

    @PostMapping("/{cashBoxId}/cierres")
    @PreAuthorize("hasAuthority('caja.cerrar')")
    public ApiResponse<CashBoxDetailResponse> closeCashBox(
            @PathVariable Long cashBoxId,
            Authentication authentication,
            @Valid @RequestBody CloseCashBoxRequest request
    ) {
        return responseFactory.success(
                "Caja cerrada correctamente.",
                closeCashBoxUseCase.execute(cashBoxId, extractPrincipal(authentication), request)
        );
    }

    private SecurityUserPrincipal extractPrincipal(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUserPrincipal principal)) {
            throw new BusinessException(
                    ErrorCode.AUTH_INVALID_TOKEN,
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "No se pudo recuperar la sesion autenticada."
            );
        }

        return principal;
    }
}
