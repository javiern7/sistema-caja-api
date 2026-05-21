package pe.sumaq.ayllu.caja.sistemacaja.modules.reportes.infrastructure.persistence;

import org.springframework.data.jpa.domain.Specification;

public final class ReportHistorySpecifications {

    private ReportHistorySpecifications() {
    }

    public static Specification<ReportHistoryEntity> withFilters(String reportType, String generatedBy) {
        return Specification.allOf(
                hasReportType(reportType),
                hasGeneratedBy(generatedBy)
        );
    }

    private static Specification<ReportHistoryEntity> hasReportType(String reportType) {
        return (root, query, criteriaBuilder) -> {
            if (reportType == null || reportType.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("reportType")),
                    reportType.trim().toLowerCase()
            );
        };
    }

    private static Specification<ReportHistoryEntity> hasGeneratedBy(String generatedBy) {
        return (root, query, criteriaBuilder) -> {
            if (generatedBy == null || generatedBy.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("generatedBy")),
                    generatedBy.trim().toLowerCase()
            );
        };
    }
}
