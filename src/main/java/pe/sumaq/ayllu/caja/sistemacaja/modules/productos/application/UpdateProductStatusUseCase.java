package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;

@Service
public class UpdateProductStatusUseCase {

    private final JpaProductRepository jpaProductRepository;

    public UpdateProductStatusUseCase(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    public ProductEntity execute(Long productId, boolean active) {
        ProductEntity productEntity = jpaProductRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PRODUCTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el producto solicitado."
                ));

        productEntity.setActive(active);
        return jpaProductRepository.save(productEntity);
    }
}
