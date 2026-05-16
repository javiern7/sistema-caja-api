package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
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

    private final ListProvidersUseCase listProvidersUseCase;
    private final CreateProviderUseCase createProviderUseCase;
    private final UpdateProviderUseCase updateProviderUseCase;
    private final ProviderMapper providerMapper;
    private final ApiResponseFactory responseFactory;

    public ProvidersController(
            ListProvidersUseCase listProvidersUseCase,
            CreateProviderUseCase createProviderUseCase,
            UpdateProviderUseCase updateProviderUseCase,
            ProviderMapper providerMapper,
            ApiResponseFactory responseFactory
    ) {
        this.listProvidersUseCase = listProvidersUseCase;
        this.createProviderUseCase = createProviderUseCase;
        this.updateProviderUseCase = updateProviderUseCase;
        this.providerMapper = providerMapper;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<List<ProviderResponse>> listProviders() {
        return responseFactory.success(
                "Proveedores obtenidos correctamente.",
                listProvidersUseCase.execute().stream().map(providerMapper::toResponse).toList()
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
