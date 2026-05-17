package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@Service
public class ListSalesUseCase {

    private final JpaSaleRepository jpaSaleRepository;
    private final SaleMapper saleMapper;

    public ListSalesUseCase(JpaSaleRepository jpaSaleRepository, SaleMapper saleMapper) {
        this.jpaSaleRepository = jpaSaleRepository;
        this.saleMapper = saleMapper;
    }

    public List<SaleResponse> execute(SaleStatus status) {
        return (status == null
                ? jpaSaleRepository.findAllByOrderByCreatedAtDesc()
                : jpaSaleRepository.findAllByStatusOrderByCreatedAtDesc(status))
                .stream()
                .map(saleMapper::toResponse)
                .toList();
    }
}
