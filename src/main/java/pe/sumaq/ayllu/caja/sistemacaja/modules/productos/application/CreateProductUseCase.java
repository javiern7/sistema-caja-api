package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.CreateProductRequest;

@Service
public class CreateProductUseCase {

    private final JpaProductRepository jpaProductRepository;

    public CreateProductUseCase(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    public ProductEntity execute(CreateProductRequest request) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setCode(request.code());
        productEntity.setName(request.name());
        productEntity.setUnitOfMeasure(request.unitOfMeasure());
        productEntity.setSalePrice(request.salePrice());
        productEntity.setReferenceCost(request.referenceCost());
        productEntity.setMinimumStock(request.minimumStock());
        productEntity.setStockControlled(request.stockControlled());
        productEntity.setActive(request.active());
        productEntity.setDescription(request.description());
        return jpaProductRepository.save(productEntity);
    }
}
