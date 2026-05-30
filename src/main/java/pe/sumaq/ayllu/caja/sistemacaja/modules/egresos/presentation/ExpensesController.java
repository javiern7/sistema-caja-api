package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation;

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
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application.CreateExpenseUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application.GetExpenseDetailUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application.ListExpensesUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.CreateExpenseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;

@RestController
@RequestMapping("/api/v1/egresos")
public class ExpensesController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "expenseDate",
            "expenseType",
            "category",
            "amount",
            "createdAt"
    );

    private final CreateExpenseUseCase createExpenseUseCase;
    private final GetExpenseDetailUseCase getExpenseDetailUseCase;
    private final ListExpensesUseCase listExpensesUseCase;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public ExpensesController(
            CreateExpenseUseCase createExpenseUseCase,
            GetExpenseDetailUseCase getExpenseDetailUseCase,
            ListExpensesUseCase listExpensesUseCase,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.createExpenseUseCase = createExpenseUseCase;
        this.getExpenseDetailUseCase = getExpenseDetailUseCase;
        this.listExpensesUseCase = listExpensesUseCase;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('egreso.registrar')")
    public ApiResponse<ExpenseResponse> createExpense(
            Authentication authentication,
            @Valid @RequestBody CreateExpenseRequest request
    ) {
        return responseFactory.success(
                "Egreso registrado correctamente.",
                createExpenseUseCase.execute(extractPrincipal(authentication), request)
        );
    }

    @GetMapping("/{expenseId}")
    @PreAuthorize("hasAuthority('egreso.registrar')")
    public ApiResponse<ExpenseResponse> getExpenseDetail(@PathVariable Long expenseId) {
        return responseFactory.success(
                "Detalle de egreso obtenido correctamente.",
                getExpenseDetailUseCase.execute(expenseId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('egreso.registrar')")
    public ApiResponse<PageResponse<ExpenseResponse>> listExpenses(
            @RequestParam(required = false) Long operationalContextId,
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
                "Egresos obtenidos correctamente.",
                PageResponse.from(listExpensesUseCase.execute(operationalContextId, pageable))
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
