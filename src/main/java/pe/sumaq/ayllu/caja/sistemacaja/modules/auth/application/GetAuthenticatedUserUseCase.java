package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.application;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain.AuthenticatedUser;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;

@Service
public class GetAuthenticatedUserUseCase {

    public AuthenticatedUser execute(Authentication authentication) {
        SecurityUserPrincipal principal = (SecurityUserPrincipal) authentication.getPrincipal();
        return principal.toAuthenticatedUser();
    }
}
