package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.JpaReportHistoryRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.presentation.dto.ReportHistoryResponse;

@Service
public class GetReportHistoryUseCase {

    private final JpaReportHistoryRepository jpaReportHistoryRepository;

    public GetReportHistoryUseCase(JpaReportHistoryRepository jpaReportHistoryRepository) {
        this.jpaReportHistoryRepository = jpaReportHistoryRepository;
    }

    public List<ReportHistoryResponse> execute(String reportType, String generatedBy) {
        return jpaReportHistoryRepository.findAllByOrderByGeneratedAtDesc().stream()
                .filter(item -> reportType == null || item.getReportType().equalsIgnoreCase(reportType))
                .filter(item -> generatedBy == null || item.getGeneratedBy().equalsIgnoreCase(generatedBy))
                .map(item -> new ReportHistoryResponse(
                        item.getId(),
                        item.getReportType(),
                        item.getFormat(),
                        item.getGeneratedBy(),
                        item.getFilters(),
                        item.getGeneratedAt()
                ))
                .toList();
    }
}
