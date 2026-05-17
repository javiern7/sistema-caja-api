package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;

public interface JpaSaleRepository extends JpaRepository<SaleEntity, Long> {

    List<SaleEntity> findAllByStatusOrderByCreatedAtDesc(SaleStatus status);

    List<SaleEntity> findAllByOrderByCreatedAtDesc();
}
