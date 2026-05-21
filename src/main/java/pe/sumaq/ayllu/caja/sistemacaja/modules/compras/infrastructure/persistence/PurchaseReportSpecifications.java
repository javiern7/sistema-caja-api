package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;

public final class PurchaseReportSpecifications {

    private PurchaseReportSpecifications() {
    }

    public static Specification<PurchaseEntity> forReport(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long operationalContextId
    ) {
        return Specification.allOf(
                isNotCancelled(),
                hasOperationalContextId(operationalContextId),
                purchaseDateFrom(fechaDesde),
                purchaseDateUntil(fechaHasta)
        );
    }

    private static Specification<PurchaseEntity> isNotCancelled() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("status"), PurchaseStatus.ANULADA);
    }

    private static Specification<PurchaseEntity> hasOperationalContextId(Long operationalContextId) {
        return (root, query, criteriaBuilder) -> {
            if (operationalContextId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("operationalContext").get("id"), operationalContextId);
        };
    }

    private static Specification<PurchaseEntity> purchaseDateFrom(LocalDate fechaDesde) {
        return (root, query, criteriaBuilder) -> {
            if (fechaDesde == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("purchaseDate"), fechaDesde);
        };
    }

    private static Specification<PurchaseEntity> purchaseDateUntil(LocalDate fechaHasta) {
        return (root, query, criteriaBuilder) -> {
            if (fechaHasta == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("purchaseDate"), fechaHasta);
        };
    }
}
