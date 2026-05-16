package pe.sumaq.ayllu.caja.sistemacaja.common.api;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;

@Component
public class ApiResponseFactory {

    public <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    public <T> ApiResponse<T> success(String message, T data, Map<String, Object> meta) {
        return ApiResponse.success(message, data, meta);
    }

    public ApiResponse<Void> error(String message, ErrorCode errorCode) {
        return ApiResponse.error(message, new ApiError(errorCode.name(), List.of()));
    }

    public ApiResponse<Void> error(String message, ErrorCode errorCode, List<String> details) {
        return ApiResponse.error(message, new ApiError(errorCode.name(), details));
    }
}
