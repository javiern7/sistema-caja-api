package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;

public interface JpaPurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    List<PurchaseEntity> findAllByStatusOrderByCreatedAtDesc(PurchaseStatus status);

    List<PurchaseEntity> findAllByOrderByCreatedAtDesc();
}
