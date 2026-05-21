package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaReportHistoryRepository extends JpaRepository<ReportHistoryEntity, Long>,
        JpaSpecificationExecutor<ReportHistoryEntity> {
}
