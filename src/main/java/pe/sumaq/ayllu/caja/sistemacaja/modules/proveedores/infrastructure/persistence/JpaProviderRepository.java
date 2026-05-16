package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProviderRepository extends JpaRepository<ProviderEntity, Long> {
}
