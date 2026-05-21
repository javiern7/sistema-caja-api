package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageableFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application.ListAuditOperationsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation.dto.AuditOperationResponse;

@RestController
@RequestMapping("/api/v1/auditoria/operaciones")
@PreAuthorize("hasAuthority('auditoria.consultar')")
public class AuditOperationsController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "module",
            "operationType",
            "entityType",
            "entityId",
            "username",
            "occurredAt"
    );

    private final ListAuditOperationsUseCase listAuditOperationsUseCase;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public AuditOperationsController(
            ListAuditOperationsUseCase listAuditOperationsUseCase,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.listAuditOperationsUseCase = listAuditOperationsUseCase;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<PageResponse<AuditOperationResponse>> listAuditOperations(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.DESC, "occurredAt"),
                ALLOWED_SORTS
        );

        return responseFactory.success(
                "Operaciones auditadas obtenidas correctamente.",
                PageResponse.from(listAuditOperationsUseCase.execute(module, username, pageable))
        );
    }
}
