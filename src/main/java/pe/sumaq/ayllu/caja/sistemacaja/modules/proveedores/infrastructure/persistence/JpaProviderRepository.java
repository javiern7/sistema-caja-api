package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProviderRepository extends JpaRepository<ProviderEntity, Long> {

    Optional<ProviderEntity> findByDocumentNumber(String documentNumber);
}
