package pe.sumaq.ayllu.caja.sistemacaja.common.api;

import java.util.List;

public record ApiError(
        String code,
        List<String> details
) {
}
