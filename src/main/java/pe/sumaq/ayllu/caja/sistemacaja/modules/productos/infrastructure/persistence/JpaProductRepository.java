package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByActive(boolean active);
}
