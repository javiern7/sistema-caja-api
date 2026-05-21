package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {

    Page<RoleEntity> findAll(Pageable pageable);

    Optional<RoleEntity> findByName(String name);
}
