package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application;

import java.util.LinkedHashSet;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaPermissionRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.UpdateRolePermissionsRequest;

@Service
public class UpdateRolePermissionsUseCase {

    private final JpaRoleRepository jpaRoleRepository;
    private final JpaPermissionRepository jpaPermissionRepository;

    public UpdateRolePermissionsUseCase(
            JpaRoleRepository jpaRoleRepository,
            JpaPermissionRepository jpaPermissionRepository
    ) {
        this.jpaRoleRepository = jpaRoleRepository;
        this.jpaPermissionRepository = jpaPermissionRepository;
    }

    public RoleEntity execute(Long roleId, UpdateRolePermissionsRequest request) {
        RoleEntity roleEntity = jpaRoleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ROL_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el rol solicitado."
                ));

        roleEntity.setPermissions(new LinkedHashSet<>(
                jpaPermissionRepository.findByCodeIn(request.permissions() == null ? java.util.List.of() : request.permissions())
        ));
        return jpaRoleRepository.save(roleEntity);
    }
}
