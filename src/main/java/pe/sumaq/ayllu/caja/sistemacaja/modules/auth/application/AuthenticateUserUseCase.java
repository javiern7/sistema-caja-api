package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.application;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain.AuthenticatedUser;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.JwtTokenService;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto.LoginRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.presentation.dto.LoginResponse;

@Service
public class AuthenticateUserUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthenticateUserUseCase(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse execute(LoginRequest request) {
        try {
            SecurityUserPrincipal principal = (SecurityUserPrincipal) authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(),
                            request.password()
                    )
            ).getPrincipal();

            AuthenticatedUser authenticatedUser = principal.toAuthenticatedUser();
            String token = jwtTokenService.generateToken(authenticatedUser);

            return new LoginResponse(token, authenticatedUser, authenticatedUser.permissions());
        } catch (DisabledException exception) {
            throw new BusinessException(
                    ErrorCode.AUTH_USER_INACTIVE,
                    HttpStatus.UNAUTHORIZED,
                    "El usuario se encuentra inactivo."
            );
        } catch (BadCredentialsException exception) {
            throw new BusinessException(
                    ErrorCode.AUTH_INVALID_CREDENTIALS,
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales invalidas."
            );
        }
    }
}
