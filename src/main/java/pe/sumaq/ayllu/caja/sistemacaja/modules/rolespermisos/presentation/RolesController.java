package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation;

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
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application.CreateRoleUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application.ListRolesUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application.RoleMapper;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application.UpdateRolePermissionsUseCase;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.CreateRoleRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.RoleResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.UpdateRolePermissionsRequest;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasAuthority('rol.gestionar')")
public class RolesController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id",
            "name",
            "description"
    );

    private final ListRolesUseCase listRolesUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRolePermissionsUseCase updateRolePermissionsUseCase;
    private final RoleMapper roleMapper;
    private final PageableFactory pageableFactory;
    private final ApiResponseFactory responseFactory;

    public RolesController(
            ListRolesUseCase listRolesUseCase,
            CreateRoleUseCase createRoleUseCase,
            UpdateRolePermissionsUseCase updateRolePermissionsUseCase,
            RoleMapper roleMapper,
            PageableFactory pageableFactory,
            ApiResponseFactory responseFactory
    ) {
        this.listRolesUseCase = listRolesUseCase;
        this.createRoleUseCase = createRoleUseCase;
        this.updateRolePermissionsUseCase = updateRolePermissionsUseCase;
        this.roleMapper = roleMapper;
        this.pageableFactory = pageableFactory;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<PageResponse<RoleResponse>> listRoles(
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
                "Roles obtenidos correctamente.",
                PageResponse.from(listRolesUseCase.execute(pageable).map(roleMapper::toResponse))
        );
    }

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return responseFactory.success(
                "Rol registrado correctamente.",
                roleMapper.toResponse(createRoleUseCase.execute(request))
        );
    }

    @PutMapping("/{roleId}/permisos")
    public ApiResponse<RoleResponse> updateRolePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody UpdateRolePermissionsRequest request
    ) {
        return responseFactory.success(
                "Permisos del rol actualizados correctamente.",
                roleMapper.toResponse(updateRolePermissionsUseCase.execute(roleId, request))
        );
    }
}
