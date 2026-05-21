package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.domain.SaleStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleListResponse;

@Service
public class ListSalesUseCase {

    private final JpaSaleRepository jpaSaleRepository;
    private final SaleMapper saleMapper;

    public ListSalesUseCase(JpaSaleRepository jpaSaleRepository, SaleMapper saleMapper) {
        this.jpaSaleRepository = jpaSaleRepository;
        this.saleMapper = saleMapper;
    }

    @Transactional(readOnly = true)
    public Page<SaleListResponse> execute(SaleStatus status, Pageable pageable) {
        if (status == null) {
            return jpaSaleRepository.findAll(pageable)
                    .map(saleMapper::toListResponse);
        }

        return jpaSaleRepository.findAllByStatus(status, pageable)
                .map(saleMapper::toListResponse);
    }
}
