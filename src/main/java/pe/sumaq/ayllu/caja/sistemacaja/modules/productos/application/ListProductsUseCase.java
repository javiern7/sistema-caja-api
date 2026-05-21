package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;

@Service
public class ListProductsUseCase {

    private final JpaProductRepository jpaProductRepository;

    public ListProductsUseCase(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    public Page<ProductEntity> execute(Boolean active, Pageable pageable) {
        if (active == null) {
            return jpaProductRepository.findAll(pageable);
        }
        return jpaProductRepository.findAllByActive(active, pageable);
    }
}
