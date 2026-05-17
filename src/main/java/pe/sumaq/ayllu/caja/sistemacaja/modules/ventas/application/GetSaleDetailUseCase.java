package pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.presentation.dto.SaleResponse;

@Service
public class GetSaleDetailUseCase {

    private final JpaSaleRepository jpaSaleRepository;
    private final SaleMapper saleMapper;

    public GetSaleDetailUseCase(JpaSaleRepository jpaSaleRepository, SaleMapper saleMapper) {
        this.jpaSaleRepository = jpaSaleRepository;
        this.saleMapper = saleMapper;
    }

    public SaleResponse execute(Long saleId) {
        return jpaSaleRepository.findById(saleId)
                .map(saleMapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.VENTA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la venta solicitada."
                ));
    }
}
