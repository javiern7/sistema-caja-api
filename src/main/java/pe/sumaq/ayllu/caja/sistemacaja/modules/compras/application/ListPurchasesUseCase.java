package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseListResponse;

@Service
public class ListPurchasesUseCase {

    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final PurchaseMapper purchaseMapper;

    public ListPurchasesUseCase(JpaPurchaseRepository jpaPurchaseRepository, PurchaseMapper purchaseMapper) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Transactional(readOnly = true)
    public Page<PurchaseListResponse> execute(PurchaseStatus status, Long operationalContextId, Pageable pageable) {
        if (operationalContextId != null) {
            if (status == null) {
                return jpaPurchaseRepository.findAllByOperationalContextId(operationalContextId, pageable)
                        .map(purchaseMapper::toListResponse);
            }

            return jpaPurchaseRepository.findAllByStatusAndOperationalContextId(status, operationalContextId, pageable)
                    .map(purchaseMapper::toListResponse);
        }

        if (status == null) {
            return jpaPurchaseRepository.findAll(pageable)
                    .map(purchaseMapper::toListResponse);
        }

        return jpaPurchaseRepository.findAllByStatus(status, pageable)
                .map(purchaseMapper::toListResponse);
    }
}
