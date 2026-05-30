package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;

public interface JpaPurchaseRepository extends JpaRepository<PurchaseEntity, Long>, JpaSpecificationExecutor<PurchaseEntity> {

    Page<PurchaseEntity> findAllByStatus(PurchaseStatus status, Pageable pageable);

    Page<PurchaseEntity> findAllByOperationalContextId(Long operationalContextId, Pageable pageable);

    Page<PurchaseEntity> findAllByStatusAndOperationalContextId(PurchaseStatus status, Long operationalContextId, Pageable pageable);

    List<PurchaseEntity> findAllByOrderByCreatedAtDesc();

    Optional<PurchaseEntity> findByDocumentNumber(String documentNumber);
}
