package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation;

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
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application.CreateProviderUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application.ListProvidersUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application.ProviderMapper;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application.UpdateProviderUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.CreateProviderRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.ProviderResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.UpdateProviderRequest;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProvidersController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "name",
            "documentNumber",
            "contactName",
            "active"
    );

    private final ListProvidersUseCase listProvidersUseCase;
    private final CreateProviderUseCase createProviderUseCase;
    private final UpdateProviderUseCase updateProviderUseCase;
    private final ProviderMapper providerMapper;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public ProvidersController(
            ListProvidersUseCase listProvidersUseCase,
            CreateProviderUseCase createProviderUseCase,
            UpdateProviderUseCase updateProviderUseCase,
            ProviderMapper providerMapper,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.listProvidersUseCase = listProvidersUseCase;
        this.createProviderUseCase = createProviderUseCase;
        this.updateProviderUseCase = updateProviderUseCase;
        this.providerMapper = providerMapper;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('proveedor.gestionar', 'compra.registrar')")
    public ApiResponse<PageResponse<ProviderResponse>> listProviders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> sort
    ) {
        Pageable pageable = pageableFactory.create(
                page,
                size,
                sort,
                Sort.by(Sort.Direction.ASC, "name"),
                ALLOWED_SORTS
        );

        return responseFactory.success(
                "Proveedores obtenidos correctamente.",
                PageResponse.from(listProvidersUseCase.execute(pageable).map(providerMapper::toResponse))
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('proveedor.gestionar')")
    public ApiResponse<ProviderResponse> createProvider(@Valid @RequestBody CreateProviderRequest request) {
        return responseFactory.success(
                "Proveedor registrado correctamente.",
                providerMapper.toResponse(createProviderUseCase.execute(request))
        );
    }

    @PutMapping("/{providerId}")
    @PreAuthorize("hasAuthority('proveedor.gestionar')")
    public ApiResponse<ProviderResponse> updateProvider(
            @PathVariable Long providerId,
            @Valid @RequestBody UpdateProviderRequest request
    ) {
        return responseFactory.success(
                "Proveedor actualizado correctamente.",
                providerMapper.toResponse(updateProviderUseCase.execute(providerId, request))
        );
    }
}
