package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, String> {

    List<PermissionEntity> findByCodeIn(List<String> codes);
}
