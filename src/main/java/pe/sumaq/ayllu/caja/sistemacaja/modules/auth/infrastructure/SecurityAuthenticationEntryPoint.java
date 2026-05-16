package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@Component
public class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    public SecurityAuthenticationEntryPoint(SecurityErrorResponseWriter securityErrorResponseWriter) {
        this.securityErrorResponseWriter = securityErrorResponseWriter;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        securityErrorResponseWriter.write(
                response,
                HttpStatus.UNAUTHORIZED,
                "La operacion requiere autenticacion valida.",
                ErrorCode.AUTH_INVALID_TOKEN
        );
    }
}
