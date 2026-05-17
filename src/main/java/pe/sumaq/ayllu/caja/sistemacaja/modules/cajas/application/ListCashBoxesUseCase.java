package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;

@Service
public class ListCashBoxesUseCase {

    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final CashBoxMapper cashBoxMapper;

    public ListCashBoxesUseCase(
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            CashBoxMapper cashBoxMapper
    ) {
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.cashBoxMapper = cashBoxMapper;
    }

    public List<CashBoxDetailResponse> execute(CashBoxStatus status, Long operationalContextId, Long openedByUserId) {
        return jpaCashBoxRepository.findAllByOrderByOpenedAtDesc()
                .stream()
                .filter(item -> status == null || item.getStatus() == status)
                .filter(item -> operationalContextId == null || item.getOperationalContext().getId().equals(operationalContextId))
                .filter(item -> openedByUserId == null || item.getOpenedBy().getId().equals(openedByUserId))
                .map(item -> cashBoxMapper.toDetailResponse(
                        item,
                        jpaCashMovementRepository.findAllByCashBoxIdOrderByOccurredAtAsc(item.getId())
                ))
                .toList();
    }
}
