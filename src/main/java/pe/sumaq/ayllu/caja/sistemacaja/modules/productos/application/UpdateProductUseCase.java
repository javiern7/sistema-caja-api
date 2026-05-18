package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application.StockCatalogSynchronizer;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.presentation.dto.UpdateProductRequest;

@Service
public class UpdateProductUseCase {

    private final JpaProductRepository jpaProductRepository;
    private final StockCatalogSynchronizer stockCatalogSynchronizer;

    public UpdateProductUseCase(
            JpaProductRepository jpaProductRepository,
            StockCatalogSynchronizer stockCatalogSynchronizer
    ) {
        this.jpaProductRepository = jpaProductRepository;
        this.stockCatalogSynchronizer = stockCatalogSynchronizer;
    }

    @Transactional
    public ProductEntity execute(Long productId, UpdateProductRequest request) {
        ProductEntity productEntity = jpaProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el producto solicitado."
                ));

        productEntity.setCode(request.code());
        productEntity.setName(request.name());
        productEntity.setUnitOfMeasure(request.unitOfMeasure());
        productEntity.setSalePrice(request.salePrice());
        productEntity.setReferenceCost(request.referenceCost());
        productEntity.setMinimumStock(request.minimumStock());
        productEntity.setStockControlled(request.stockControlled());
        productEntity.setActive(request.active());
        productEntity.setDescription(request.description());
        ProductEntity savedProduct = jpaProductRepository.saveAndFlush(productEntity);
        stockCatalogSynchronizer.ensureCurrentStockRow(savedProduct);
        return savedProduct;
    }
}
