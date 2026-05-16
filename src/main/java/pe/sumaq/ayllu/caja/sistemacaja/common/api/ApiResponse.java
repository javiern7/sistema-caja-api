package pe.sumaq.ayllu.caja.sistemacaja.common.api;

import java.util.Map;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Map<String, Object> meta,
        ApiError error
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Map.of(), null);
    }

    public static <T> ApiResponse<T> success(String message, T data, Map<String, Object> meta) {
        return new ApiResponse<>(true, message, data, meta, null);
    }

    public static <T> ApiResponse<T> error(String message, ApiError error) {
        return new ApiResponse<>(false, message, null, null, error);
    }
}
