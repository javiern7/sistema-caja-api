package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;

@Service
public class GetPurchaseDetailUseCase {

    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final PurchaseMapper purchaseMapper;

    public GetPurchaseDetailUseCase(JpaPurchaseRepository jpaPurchaseRepository, PurchaseMapper purchaseMapper) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Transactional(readOnly = true)
    public PurchaseResponse execute(Long purchaseId) {
        return jpaPurchaseRepository.findById(purchaseId)
                .map(purchaseMapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.COMPRA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la compra solicitada."
                ));
    }
}
