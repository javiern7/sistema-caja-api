package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence;

import org.springframework.data.jpa.domain.Specification;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;

public final class CashBoxSpecifications {

    private CashBoxSpecifications() {
    }

    public static Specification<CashBoxEntity> withFilters(
            CashBoxStatus status,
            Long operationalContextId,
            Long openedByUserId
    ) {
        return Specification.allOf(
                hasStatus(status),
                hasOperationalContextId(operationalContextId),
                hasOpenedByUserId(openedByUserId)
        );
    }

    private static Specification<CashBoxEntity> hasStatus(CashBoxStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    private static Specification<CashBoxEntity> hasOperationalContextId(Long operationalContextId) {
        return (root, query, criteriaBuilder) -> {
            if (operationalContextId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("operationalContext").get("id"), operationalContextId);
        };
    }

    private static Specification<CashBoxEntity> hasOpenedByUserId(Long openedByUserId) {
        return (root, query, criteriaBuilder) -> {
            if (openedByUserId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("openedBy").get("id"), openedByUserId);
        };
    }
}
