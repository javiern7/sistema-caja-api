package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation.dto;

import java.time.LocalDateTime;

public record AuditOperationResponse(
        Long id,
        String module,
        String operationType,
        String entityType,
        String entityId,
        String username,
        LocalDateTime occurredAt,
        String detail
) {
}
