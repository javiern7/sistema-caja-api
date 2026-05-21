package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.ProductEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.StockCurrentEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockCurrentResponse;

@Service
public class ListCurrentStockUseCase {

    private final JpaProductRepository jpaProductRepository;
    private final JpaStockCurrentRepository jpaStockCurrentRepository;
    private final StockMapper stockMapper;

    public ListCurrentStockUseCase(
            JpaProductRepository jpaProductRepository,
            JpaStockCurrentRepository jpaStockCurrentRepository,
            StockMapper stockMapper
    ) {
        this.jpaProductRepository = jpaProductRepository;
        this.jpaStockCurrentRepository = jpaStockCurrentRepository;
        this.stockMapper = stockMapper;
    }

    @Transactional(readOnly = true)
    public Page<StockCurrentResponse> execute(Boolean active, Pageable pageable) {
        Page<ProductEntity> productsPage = active == null
                ? jpaProductRepository.findAll(pageable)
                : jpaProductRepository.findAllByActive(active, pageable);

        Map<Long, StockCurrentEntity> currentStockByProductId = jpaStockCurrentRepository.findAllByProductIdIn(
                        productsPage.getContent().stream()
                                .map(ProductEntity::getId)
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(StockCurrentEntity::getProductId, Function.identity()));

        return productsPage.map(product ->
                stockMapper.toCurrentResponse(product, currentStockByProductId.get(product.getId()))
        );
    }
}
