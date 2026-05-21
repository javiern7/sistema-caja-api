package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProviderRepository extends JpaRepository<ProviderEntity, Long> {

    Page<ProviderEntity> findAll(Pageable pageable);

    Optional<ProviderEntity> findByDocumentNumber(String documentNumber);
}
