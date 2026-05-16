package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;

@Service
public class ListRolesUseCase {

    private final JpaRoleRepository jpaRoleRepository;

    public ListRolesUseCase(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    public List<RoleEntity> execute() {
        return jpaRoleRepository.findAll();
    }
}
