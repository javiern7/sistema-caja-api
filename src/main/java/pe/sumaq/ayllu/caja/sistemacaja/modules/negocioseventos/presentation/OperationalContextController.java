package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation;

import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.pagination.PageableFactory;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.CreateOperationalContextUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.ListAvailableOperationalContextsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.ListOperationalContextsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.application.UpdateOperationalContextUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.CreateOperationalContextRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.OperationalContextResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.presentation.dto.UpdateOperationalContextRequest;

@RestController
@RequestMapping("/api/v1")
public class OperationalContextController {

    private static final Set<String> ALLOWED_ADMIN_SORTS = Set.of(
            "id",
            "code",
            "name",
            "type",
            "status",
            "startDate",
            "endDate"
    );

    private final ListAvailableOperationalContextsUseCase listAvailableOperationalContextsUseCase;
    private final ListOperationalContextsUseCase listOperationalContextsUseCase;
    private final CreateOperationalContextUseCase createOperationalContextUseCase;
    private final UpdateOperationalContextUseCase updateOperationalContextUseCase;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public OperationalContextController(
            ListAvailableOperationalContextsUseCase listAvailableOperationalContextsUseCase,
            ListOperationalContextsUseCase listOperationalContextsUseCase,
            CreateOperationalContextUseCase createOperationalContextUseCase,
            UpdateOperationalContextUseCase updateOperationalContextUseCase,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.listAvailableOperationalContextsUseCase = listAvailableOperationalContextsUseCase;
        this.listOperationalContextsUseCase = listOperationalContextsUseCase;
        this.createOperationalContextUseCase = createOperationalContextUseCase;
        this.updateOperationalContextUseCase = updateOperationalContextUseCase;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping("/contextos-operativos")
    @PreAuthorize("hasAnyAuthority('negocioevento.gestionar', 'caja.abrir', 'venta.registrar', 'compra.registrar', 'egreso.registrar')")
    public ApiResponse<List<OperationalContextResponse>> listAvailableOperationalContexts() {
        return responseFactory.success(
                "Contextos operativos disponibles obtenidos correctamente.",
                toResponseList(listAvailableOperationalContextsUseCase.execute())
        );
    }

    @GetMapping("/negocios-eventos")
    @PreAuthorize("hasAuthority('negocioevento.gestionar')")
    public ApiResponse<PageResponse<OperationalContextResponse>> listOperationalContexts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.ASC, "startDate").and(Sort.by(Sort.Direction.ASC, "name")),
                ALLOWED_ADMIN_SORTS
        );

        return responseFactory.success(
                "Negocios/eventos obtenidos correctamente.",
                PageResponse.from(listOperationalContextsUseCase.execute(pageable).map(this::toResponse))
        );
    }

    @PostMapping("/negocios-eventos")
    @PreAuthorize("hasAuthority('negocioevento.gestionar')")
    public ApiResponse<OperationalContextResponse> createOperationalContext(
            @Valid @RequestBody CreateOperationalContextRequest request
    ) {
        return responseFactory.success(
                "Negocio/evento registrado correctamente.",
                toResponse(createOperationalContextUseCase.execute(request))
        );
    }

    @PutMapping("/negocios-eventos/{operationalContextId}")
    @PreAuthorize("hasAuthority('negocioevento.gestionar')")
    public ApiResponse<OperationalContextResponse> updateOperationalContext(
            @PathVariable Long operationalContextId,
            @Valid @RequestBody UpdateOperationalContextRequest request
    ) {
        return responseFactory.success(
                "Negocio/evento actualizado correctamente.",
                toResponse(updateOperationalContextUseCase.execute(operationalContextId, request))
        );
    }

    private List<OperationalContextResponse> toResponseList(List<OperationalContext> operationalContexts) {
        return operationalContexts.stream()
                .map(this::toResponse)
                .toList();
    }

    private OperationalContextResponse toResponse(OperationalContext operationalContext) {
        return new OperationalContextResponse(
                operationalContext.id(),
                operationalContext.code(),
                operationalContext.name(),
                operationalContext.type(),
                operationalContext.status(),
                operationalContext.startDate(),
                operationalContext.endDate(),
                operationalContext.description()
        );
    }
}
