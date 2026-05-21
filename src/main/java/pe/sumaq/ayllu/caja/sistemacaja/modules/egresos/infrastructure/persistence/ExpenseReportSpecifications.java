package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

public final class ExpenseReportSpecifications {

    private ExpenseReportSpecifications() {
    }

    public static Specification<ExpenseEntity> forReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId
    ) {
        return Specification.allOf(
                hasOperationalContextId(operationalContextId),
                expenseDateFrom(fechaDesde),
                expenseDateUntil(fechaHasta)
        );
    }

    private static Specification<ExpenseEntity> hasOperationalContextId(Long operationalContextId) {
        return (root, query, criteriaBuilder) -> {
            if (operationalContextId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("operationalContext").get("id"), operationalContextId);
        };
    }

    private static Specification<ExpenseEntity> expenseDateFrom(LocalDate fechaDesde) {
        return (root, query, criteriaBuilder) -> {
            if (fechaDesde == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("expenseDate"), fechaDesde);
        };
    }

    private static Specification<ExpenseEntity> expenseDateUntil(LocalDate fechaHasta) {
        return (root, query, criteriaBuilder) -> {
            if (fechaHasta == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("expenseDate"), fechaHasta);
        };
    }
}
