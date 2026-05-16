package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
        @NotBlank(message = "El codigo es obligatorio.")
        String code,
        @NotBlank(message = "El nombre es obligatorio.")
        String name,
        @NotBlank(message = "La unidad de medida es obligatoria.")
        String unitOfMeasure,
        @NotNull(message = "El precio de venta es obligatorio.")
        @DecimalMin(value = "0.00", message = "El precio de venta no puede ser negativo.")
        BigDecimal salePrice,
        @NotNull(message = "El costo referencial es obligatorio.")
        @DecimalMin(value = "0.00", message = "El costo referencial no puede ser negativo.")
        BigDecimal referenceCost,
        @NotNull(message = "El stock minimo es obligatorio.")
        @DecimalMin(value = "0.00", message = "El stock minimo no puede ser negativo.")
        BigDecimal minimumStock,
        boolean stockControlled,
        boolean active,
        String description
) {
}
