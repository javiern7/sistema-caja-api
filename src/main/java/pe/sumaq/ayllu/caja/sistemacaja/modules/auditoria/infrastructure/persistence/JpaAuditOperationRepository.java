package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAuditOperationRepository extends JpaRepository<AuditOperationEntity, Long> {

    List<AuditOperationEntity> findAllByOrderByOccurredAtDesc();
}
