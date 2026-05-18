package pe.sumaq.ayllu.caja.sistemacaja.modules.compras.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.domain.PurchaseStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.presentation.dto.PurchaseResponse;

@Service
public class ListPurchasesUseCase {

    private final JpaPurchaseRepository jpaPurchaseRepository;
    private final PurchaseMapper purchaseMapper;

    public ListPurchasesUseCase(JpaPurchaseRepository jpaPurchaseRepository, PurchaseMapper purchaseMapper) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> execute(PurchaseStatus status) {
        return (status == null
                ? jpaPurchaseRepository.findAllByOrderByCreatedAtDesc()
                : jpaPurchaseRepository.findAllByStatusOrderByCreatedAtDesc(status))
                .stream()
                .map(purchaseMapper::toResponse)
                .toList();
    }
}
