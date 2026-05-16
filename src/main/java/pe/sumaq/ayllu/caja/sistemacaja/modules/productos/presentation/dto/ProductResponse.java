package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String code,
        String name,
        String unitOfMeasure,
        BigDecimal salePrice,
        BigDecimal referenceCost,
        BigDecimal minimumStock,
        boolean stockControlled,
        boolean active,
        String description
) {
}
