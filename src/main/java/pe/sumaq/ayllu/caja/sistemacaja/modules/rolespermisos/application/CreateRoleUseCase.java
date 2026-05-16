package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application;

import java.util.LinkedHashSet;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaPermissionRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto.CreateRoleRequest;

@Service
public class CreateRoleUseCase {

    private final JpaRoleRepository jpaRoleRepository;
    private final JpaPermissionRepository jpaPermissionRepository;

    public CreateRoleUseCase(
            JpaRoleRepository jpaRoleRepository,
            JpaPermissionRepository jpaPermissionRepository
    ) {
        this.jpaRoleRepository = jpaRoleRepository;
        this.jpaPermissionRepository = jpaPermissionRepository;
    }

    public RoleEntity execute(CreateRoleRequest request) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(request.name());
        roleEntity.setDescription(request.description());
        roleEntity.setPermissions(new LinkedHashSet<>(
                jpaPermissionRepository.findByCodeIn(request.permissions() == null ? java.util.List.of() : request.permissions())
        ));
        return jpaRoleRepository.save(roleEntity);
    }
}
