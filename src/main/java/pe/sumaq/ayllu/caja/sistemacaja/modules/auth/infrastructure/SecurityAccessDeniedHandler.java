package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@Component
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorResponseWriter securityErrorResponseWriter;

    public SecurityAccessDeniedHandler(SecurityErrorResponseWriter securityErrorResponseWriter) {
        this.securityErrorResponseWriter = securityErrorResponseWriter;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        securityErrorResponseWriter.write(
                response,
                HttpStatus.FORBIDDEN,
                "No cuenta con permisos para realizar esta operacion.",
                ErrorCode.FORBIDDEN_OPERATION
        );
    }
}
