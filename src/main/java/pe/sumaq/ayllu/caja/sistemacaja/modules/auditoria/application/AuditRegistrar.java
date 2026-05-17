package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.AuditOperationEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.JpaAuditOperationRepository;

@Service
public class AuditRegistrar {

    private final JpaAuditOperationRepository jpaAuditOperationRepository;

    public AuditRegistrar(JpaAuditOperationRepository jpaAuditOperationRepository) {
        this.jpaAuditOperationRepository = jpaAuditOperationRepository;
    }

    public void record(
            String module,
            String operationType,
            String entityType,
            String entityId,
            String username,
            String detail
    ) {
        AuditOperationEntity entity = new AuditOperationEntity();
        entity.setModule(module);
        entity.setOperationType(operationType);
        entity.setEntityType(entityType);
        entity.setEntityId(entityId);
        entity.setUsername(username);
        entity.setOccurredAt(LocalDateTime.now());
        entity.setDetail(detail);
        jpaAuditOperationRepository.save(entity);
    }
}
