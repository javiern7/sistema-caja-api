package pe.sumaq.ayllu.caja.sistemacaja.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;
    private final List<String> details;

    public BusinessException(ErrorCode errorCode, HttpStatus status, String message) {
        this(errorCode, status, message, List.of());
    }

    public BusinessException(ErrorCode errorCode, HttpStatus status, String message, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<String> getDetails() {
        return details;
    }
}
