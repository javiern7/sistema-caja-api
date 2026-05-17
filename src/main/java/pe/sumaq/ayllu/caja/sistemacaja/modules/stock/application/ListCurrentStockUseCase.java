package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

    public List<StockCurrentResponse> execute() {
        Map<Long, StockCurrentEntity> currentStockByProductId = jpaStockCurrentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(StockCurrentEntity::getProductId, Function.identity()));

        return jpaProductRepository.findAll()
                .stream()
                .map(product -> stockMapper.toCurrentResponse(product, currentStockByProductId.get(product.getId())))
                .toList();
    }
}
