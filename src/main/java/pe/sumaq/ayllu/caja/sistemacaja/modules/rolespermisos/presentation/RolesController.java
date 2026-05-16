package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation;

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

    private final ListRolesUseCase listRolesUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRolePermissionsUseCase updateRolePermissionsUseCase;
    private final RoleMapper roleMapper;
    private final ApiResponseFactory responseFactory;

    public RolesController(
            ListRolesUseCase listRolesUseCase,
            CreateRoleUseCase createRoleUseCase,
            UpdateRolePermissionsUseCase updateRolePermissionsUseCase,
            RoleMapper roleMapper,
            ApiResponseFactory responseFactory
    ) {
        this.listRolesUseCase = listRolesUseCase;
        this.createRoleUseCase = createRoleUseCase;
        this.updateRolePermissionsUseCase = updateRolePermissionsUseCase;
        this.roleMapper = roleMapper;
        this.responseFactory = responseFactory;
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> listRoles() {
        return responseFactory.success(
                "Roles obtenidos correctamente.",
                listRolesUseCase.execute().stream().map(roleMapper::toResponse).toList()
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
