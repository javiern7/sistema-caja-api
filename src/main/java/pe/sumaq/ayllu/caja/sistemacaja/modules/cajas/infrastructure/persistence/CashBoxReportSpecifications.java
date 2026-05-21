package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

public final class CashBoxReportSpecifications {

    private CashBoxReportSpecifications() {
    }

    public static Specification<CashBoxEntity> forReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId
    ) {
        return Specification.allOf(
                hasOperationalContextId(operationalContextId),
                openedAtFrom(fechaDesde),
                openedAtUntil(fechaHasta)
        );
    }

    private static Specification<CashBoxEntity> hasOperationalContextId(Long operationalContextId) {
        return (root, query, criteriaBuilder) -> {
            if (operationalContextId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("operationalContext").get("id"), operationalContextId);
        };
    }

    private static Specification<CashBoxEntity> openedAtFrom(LocalDate fechaDesde) {
        return (root, query, criteriaBuilder) -> {
            if (fechaDesde == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("openedAt"), fechaDesde.atStartOfDay());
        };
    }

    private static Specification<CashBoxEntity> openedAtUntil(LocalDate fechaHasta) {
        return (root, query, criteriaBuilder) -> {
            if (fechaHasta == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("openedAt"), fechaHasta.atTime(LocalTime.MAX));
        };
    }
}
