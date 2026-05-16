package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain;

import java.util.List;

public record AuthenticatedUser(
        Long id,
        String username,
        String role,
        boolean active,
        List<String> permissions
) {
}
