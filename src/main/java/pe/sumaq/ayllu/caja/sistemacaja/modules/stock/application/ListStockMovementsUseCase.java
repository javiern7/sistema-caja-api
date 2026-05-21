package pe.sumaq.ayllu.caja.sistemacaja.modules.stock.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<StockMovementResponse> execute(Pageable pageable) {
        return jpaStockMovementRepository.findAllByOrderByOccurredAtDesc(pageable)
                .map(stockMapper::toMovementResponse);
    }
}
