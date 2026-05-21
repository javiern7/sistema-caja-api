package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;

public interface JpaPurchaseRepository extends JpaRepository<PurchaseEntity, Long>, JpaSpecificationExecutor<PurchaseEntity> {

    Page<PurchaseEntity> findAllByStatus(PurchaseStatus status, Pageable pageable);

    List<PurchaseEntity> findAllByOrderByCreatedAtDesc();
}
