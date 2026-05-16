package pe.sumaq.ayllu.caja.sistemacaja.modules.productos.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;

@Service
public class ListProductsUseCase {

    private final JpaProductRepository jpaProductRepository;

    public ListProductsUseCase(JpaProductRepository jpaProductRepository) {
        this.jpaProductRepository = jpaProductRepository;
    }

    public List<ProductEntity> execute(Boolean active) {
        if (active == null) {
            return jpaProductRepository.findAll();
        }
        return jpaProductRepository.findAllByActive(active);
    }
}
