package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation;

import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageableFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.CancelPurchaseUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.CreatePurchaseUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.GetPurchaseDetailUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application.ListPurchasesUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CancelPurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.CreatePurchaseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseListResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;

@RestController
@RequestMapping("/api/v1/compras")
public class PurchasesController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "status",
            "purchaseDate",
            "documentNumber",
            "subtotalAmount",
            "totalAmount",
            "createdAt",
            "cancelledAt"
    );

    private final CreatePurchaseUseCase createPurchaseUseCase;
    private final GetPurchaseDetailUseCase getPurchaseDetailUseCase;
    private final ListPurchasesUseCase listPurchasesUseCase;
    private final CancelPurchaseUseCase cancelPurchaseUseCase;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public PurchasesController(
            CreatePurchaseUseCase createPurchaseUseCase,
            GetPurchaseDetailUseCase getPurchaseDetailUseCase,
            ListPurchasesUseCase listPurchasesUseCase,
            CancelPurchaseUseCase cancelPurchaseUseCase,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.createPurchaseUseCase = createPurchaseUseCase;
        this.getPurchaseDetailUseCase = getPurchaseDetailUseCase;
        this.listPurchasesUseCase = listPurchasesUseCase;
        this.cancelPurchaseUseCase = cancelPurchaseUseCase;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('compra.registrar')")
    public ApiResponse<PurchaseResponse> createPurchase(
            Authentication authentication,
            @Valid @RequestBody CreatePurchaseRequest request
    ) {
        return responseFactory.success(
                "Compra registrada correctamente.",
                createPurchaseUseCase.execute(extractPrincipal(authentication), request)
        );
    }

    @GetMapping("/{purchaseId}")
    @PreAuthorize("hasAuthority('compra.registrar')")
    public ApiResponse<PurchaseResponse> getPurchaseDetail(@PathVariable Long purchaseId) {
        return responseFactory.success(
                "Detalle de compra obtenido correctamente.",
                getPurchaseDetailUseCase.execute(purchaseId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('compra.registrar')")
    public ApiResponse<PageResponse<PurchaseListResponse>> listPurchases(
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "createdAt"),
                ALLOWED_SORTS
        );

        return responseFactory.success(
                "Compras obtenidas correctamente.",
                PageResponse.from(listPurchasesUseCase.execute(status, pageable))
        );
    }

    @PostMapping("/{purchaseId}/anulacion")
    @PreAuthorize("hasAuthority('compra.registrar')")
    public ApiResponse<PurchaseResponse> cancelPurchase(
            @PathVariable Long purchaseId,
            Authentication authentication,
            @Valid @RequestBody CancelPurchaseRequest request
    ) {
        return responseFactory.success(
                "Compra anulada correctamente.",
                cancelPurchaseUseCase.execute(purchaseId, extractPrincipal(authentication), request)
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
