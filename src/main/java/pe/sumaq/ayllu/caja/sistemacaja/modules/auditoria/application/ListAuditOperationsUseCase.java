package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.JpaAuditOperationRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.AuditOperationSpecifications;
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

    public Page<AuditOperationResponse> execute(String module, String username, Pageable pageable) {
        return jpaAuditOperationRepository.findAll(
                        AuditOperationSpecifications.withFilters(module, username),
                        pageable
                )
                .map(auditOperationMapper::toResponse);
    }
}
