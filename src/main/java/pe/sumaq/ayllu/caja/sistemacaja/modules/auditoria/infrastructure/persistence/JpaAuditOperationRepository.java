package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaAuditOperationRepository extends JpaRepository<AuditOperationEntity, Long>,
        JpaSpecificationExecutor<AuditOperationEntity> {
}
