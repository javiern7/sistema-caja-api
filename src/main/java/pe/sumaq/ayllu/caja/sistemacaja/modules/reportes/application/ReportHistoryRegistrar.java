package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportFormat;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.domain.ReportType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.JpaReportHistoryRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence.ReportHistoryEntity;

@Service
public class ReportHistoryRegistrar {

    private final JpaReportHistoryRepository jpaReportHistoryRepository;

    public ReportHistoryRegistrar(JpaReportHistoryRepository jpaReportHistoryRepository) {
        this.jpaReportHistoryRepository = jpaReportHistoryRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            ReportType reportType,
            ReportFormat format,
            String generatedBy,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId
    ) {
        ReportHistoryEntity entity = new ReportHistoryEntity();
        entity.setReportType(reportType.name());
        entity.setFormat(format.name());
        entity.setGeneratedBy(generatedBy);
        entity.setFilters(buildFilters(fechaDesde, fechaHasta, operationalContextId));
        entity.setGeneratedAt(LocalDateTime.now());
        jpaReportHistoryRepository.save(entity);
    }

    private String buildFilters(LocalDate fechaDesde, LocalDate fechaHasta, Long operationalContextId) {
        return "fechaDesde=" + valueOf(fechaDesde)
                + ", fechaHasta=" + valueOf(fechaHasta)
                + ", operationalContextId=" + valueOf(operationalContextId);
    }

    private static String valueOf(Object value) {
        return value == null ? "TODOS" : value.toString();
    }
}
