package pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence;

import org.springframework.data.jpa.domain.Specification;

public final class AuditOperationSpecifications {

    private AuditOperationSpecifications() {
    }

    public static Specification<AuditOperationEntity> withFilters(String module, String username) {
        return Specification.allOf(
                hasModule(module),
                hasUsername(username)
        );
    }

    private static Specification<AuditOperationEntity> hasModule(String module) {
        return (root, query, criteriaBuilder) -> {
            if (module == null || module.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("module")),
                    module.trim().toLowerCase()
            );
        };
    }

    private static Specification<AuditOperationEntity> hasUsername(String username) {
        return (root, query, criteriaBuilder) -> {
            if (username == null || username.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("username")),
                    username.trim().toLowerCase()
            );
        };
    }
}
