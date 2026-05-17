package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.presentation.dto.StockMovementResponse;

@Service
public class ListStockMovementsUseCase {

    private final JpaStockMovementRepository jpaStockMovementRepository;
    private final StockMapper stockMapper;

    public ListStockMovementsUseCase(
            JpaStockMovementRepository jpaStockMovementRepository,
            StockMapper stockMapper
    ) {
        this.jpaStockMovementRepository = jpaStockMovementRepository;
        this.stockMapper = stockMapper;
    }

    public List<StockMovementResponse> execute() {
        return jpaStockMovementRepository.findAllByOrderByOccurredAtDesc()
                .stream()
                .map(stockMapper::toMovementResponse)
                .toList();
    }
}
