package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxSpecifications;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxListResponse;

@Service
public class ListCashBoxesUseCase {

    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final CashBoxMapper cashBoxMapper;

    public ListCashBoxesUseCase(
            JpaCashBoxRepository jpaCashBoxRepository,
            CashBoxMapper cashBoxMapper
    ) {
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.cashBoxMapper = cashBoxMapper;
    }

    @Transactional(readOnly = true)
    public Page<CashBoxListResponse> execute(
            CashBoxStatus status,
            Long operationalContextId,
            Long openedByUserId,
            Pageable pageable
    ) {
        return jpaCashBoxRepository.findAll(
                        CashBoxSpecifications.withFilters(status, operationalContextId, openedByUserId),
                        pageable
                )
                .map(cashBoxMapper::toListResponse);
    }
}
