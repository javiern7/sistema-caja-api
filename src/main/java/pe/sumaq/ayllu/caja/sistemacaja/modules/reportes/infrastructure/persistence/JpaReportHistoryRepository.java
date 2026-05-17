package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReportHistoryRepository extends JpaRepository<ReportHistoryEntity, Long> {

    List<ReportHistoryEntity> findAllByOrderByGeneratedAtDesc();
}
