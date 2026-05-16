package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponse;
import pe.sumaq.ayllu.caja.sistemacaja.common.api.ApiResponseFactory;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@Component
public class SecurityErrorResponseWriter {

    private final ObjectMapper objectMapper;
    private final ApiResponseFactory responseFactory;

    public SecurityErrorResponseWriter(ObjectMapper objectMapper, ApiResponseFactory responseFactory) {
        this.objectMapper = objectMapper;
        this.responseFactory = responseFactory;
    }

    public void write(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            ErrorCode errorCode
    ) throws IOException {
        ApiResponse<Void> body = responseFactory.error(message, errorCode);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
