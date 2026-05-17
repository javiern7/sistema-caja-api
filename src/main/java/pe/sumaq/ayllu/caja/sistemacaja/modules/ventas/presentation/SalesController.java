package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application.CancelSaleUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application.CreateSaleUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application.GetSaleDetailUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application.ListSalesUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.CancelSaleRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.CreateSaleRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@RestController
@RequestMapping("/api/v1/ventas")
public class SalesController {

    private final CreateSaleUseCase createSaleUseCase;
    private final GetSaleDetailUseCase getSaleDetailUseCase;
    private final CancelSaleUseCase cancelSaleUseCase;
    private final ListSalesUseCase listSalesUseCase;
    private final ApiResponseFactory responseFactory;

    public SalesController(
            CreateSaleUseCase createSaleUseCase,
            GetSaleDetailUseCase getSaleDetailUseCase,
            CancelSaleUseCase cancelSaleUseCase,
            ListSalesUseCase listSalesUseCase,
            ApiResponseFactory responseFactory
    ) {
        this.createSaleUseCase = createSaleUseCase;
        this.getSaleDetailUseCase = getSaleDetailUseCase;
        this.cancelSaleUseCase = cancelSaleUseCase;
        this.listSalesUseCase = listSalesUseCase;
        this.responseFactory = responseFactory;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('venta.registrar')")
    public ApiResponse<SaleResponse> createSale(
            Authentication authentication,
            @Valid @RequestBody CreateSaleRequest request
    ) {
        return responseFactory.success(
                "Venta registrada correctamente.",
                createSaleUseCase.execute(extractPrincipal(authentication), request)
        );
    }

    @GetMapping("/{saleId}")
    public ApiResponse<SaleResponse> getSaleDetail(@PathVariable Long saleId) {
        return responseFactory.success(
                "Detalle de venta obtenido correctamente.",
                getSaleDetailUseCase.execute(saleId)
        );
    }

    @PostMapping("/{saleId}/anulacion")
    @PreAuthorize("hasAuthority('venta.anular')")
    public ApiResponse<SaleResponse> cancelSale(
            @PathVariable Long saleId,
            Authentication authentication,
            @Valid @RequestBody CancelSaleRequest request
    ) {
        return responseFactory.success(
                "Venta anulada correctamente.",
                cancelSaleUseCase.execute(saleId, extractPrincipal(authentication), request)
        );
    }

    @GetMapping
    public ApiResponse<List<SaleResponse>> listSales(@RequestParam(required = false) SaleStatus status) {
        return responseFactory.success(
                "Ventas obtenidas correctamente.",
                listSalesUseCase.execute(status)
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
