package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.JpaAuditOperationRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.presentation.dto.AuditOperationResponse;

@Service
public class ListAuditOperationsUseCase {

    private final JpaAuditOperationRepository jpaAuditOperationRepository;
    private final AuditOperationMapper auditOperationMapper;

    public ListAuditOperationsUseCase(
            JpaAuditOperationRepository jpaAuditOperationRepository,
            AuditOperationMapper auditOperationMapper
    ) {
        this.jpaAuditOperationRepository = jpaAuditOperationRepository;
        this.auditOperationMapper = auditOperationMapper;
    }

    public List<AuditOperationResponse> execute(String module, String username) {
        return jpaAuditOperationRepository.findAllByOrderByOccurredAtDesc()
                .stream()
                .filter(item -> module == null || item.getModule().equalsIgnoreCase(module))
                .filter(item -> username == null || item.getUsername().equalsIgnoreCase(username))
                .map(auditOperationMapper::toResponse)
                .toList();
    }
}
