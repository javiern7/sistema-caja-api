package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.ProductResponse;

@Component
public class ProductMapper {

    public ProductResponse toResponse(ProductEntity productEntity) {
        return new ProductResponse(
                productEntity.getId(),
                productEntity.getCode(),
                productEntity.getName(),
                productEntity.getUnitOfMeasure(),
                productEntity.getSalePrice(),
                productEntity.getReferenceCost(),
                productEntity.getMinimumStock(),
                productEntity.isStockControlled(),
                productEntity.isActive(),
                productEntity.getDescription()
        );
    }
}
