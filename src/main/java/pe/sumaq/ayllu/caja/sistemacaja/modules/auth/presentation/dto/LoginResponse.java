package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto;

import java.util.List;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain.AuthenticatedUser;

public record LoginResponse(
        String token,
        AuthenticatedUser user,
        List<String> permissions
) {
}
