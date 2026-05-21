package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByActive(boolean active);

    Page<ProductEntity> findAllByActive(boolean active, Pageable pageable);

    Optional<ProductEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
