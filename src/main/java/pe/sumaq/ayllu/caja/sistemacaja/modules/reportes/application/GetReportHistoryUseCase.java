package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.JpaReportHistoryRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.ReportHistorySpecifications;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ReportHistoryResponse;

@Service
public class GetReportHistoryUseCase {

    private final JpaReportHistoryRepository jpaReportHistoryRepository;

    public GetReportHistoryUseCase(JpaReportHistoryRepository jpaReportHistoryRepository) {
        this.jpaReportHistoryRepository = jpaReportHistoryRepository;
    }

    public Page<ReportHistoryResponse> execute(String reportType, String generatedBy, Pageable pageable) {
        return jpaReportHistoryRepository.findAll(
                        ReportHistorySpecifications.withFilters(reportType, generatedBy),
                        pageable
                )
                .map(item -> new ReportHistoryResponse(
                        item.getId(),
                        item.getReportType(),
                        item.getFormat(),
                        item.getGeneratedBy(),
                        item.getFilters(),
                        item.getGeneratedAt()
                ));
    }
}
