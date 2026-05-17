package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.AuditOperationEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation.dto.AuditOperationResponse;

@Component
public class AuditOperationMapper {

    public AuditOperationResponse toResponse(AuditOperationEntity entity) {
        return new AuditOperationResponse(
                entity.getId(),
                entity.getModule(),
                entity.getOperationType(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getUsername(),
                entity.getOccurredAt(),
                entity.getDetail()
        );
    }
}
