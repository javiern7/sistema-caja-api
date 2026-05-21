package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;

@Service
public class ListRolesUseCase {

    private final JpaRoleRepository jpaRoleRepository;

    public ListRolesUseCase(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    public Page<RoleEntity> execute(Pageable pageable) {
        return jpaRoleRepository.findAll(pageable);
    }
}
