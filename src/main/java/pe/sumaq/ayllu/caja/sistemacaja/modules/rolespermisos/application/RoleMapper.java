package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application;

import java.util.List;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.PermissionEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.PermissionResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.RoleResponse;

@Component
public class RoleMapper {

    public RoleResponse toResponse(RoleEntity roleEntity) {
        return new RoleResponse(
                roleEntity.getId(),
                roleEntity.getName(),
                roleEntity.getDescription(),
                roleEntity.getPermissions().stream()
                        .map(this::toPermissionResponse)
                        .toList()
        );
    }

    private PermissionResponse toPermissionResponse(PermissionEntity permissionEntity) {
        return new PermissionResponse(permissionEntity.getCode(), permissionEntity.getDescription());
    }
}
