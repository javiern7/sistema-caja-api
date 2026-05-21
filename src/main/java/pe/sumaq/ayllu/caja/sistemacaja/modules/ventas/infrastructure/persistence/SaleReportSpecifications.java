package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;

public final class SaleReportSpecifications {

    private SaleReportSpecifications() {
    }

    public static Specification<SaleEntity> forReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId
    ) {
        return Specification.allOf(
                hasStatus(SaleStatus.REGISTRADA),
                hasOperationalContextId(operationalContextId),
                createdAtFrom(fechaDesde),
                createdAtUntil(fechaHasta)
        );
    }

    private static Specification<SaleEntity> hasStatus(SaleStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<SaleEntity> hasOperationalContextId(Long operationalContextId) {
        return (root, query, criteriaBuilder) -> {
            if (operationalContextId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("operationalContext").get("id"), operationalContextId);
        };
    }

    private static Specification<SaleEntity> createdAtFrom(LocalDate fechaDesde) {
        return (root, query, criteriaBuilder) -> {
            if (fechaDesde == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fechaDesde.atStartOfDay());
        };
    }

    private static Specification<SaleEntity> createdAtUntil(LocalDate fechaHasta) {
        return (root, query, criteriaBuilder) -> {
            if (fechaHasta == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), fechaHasta.atTime(LocalTime.MAX));
        };
    }
}
