package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;

@Service
public class GetCashBoxSummaryUseCase {

    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final CashBoxMapper cashBoxMapper;

    public GetCashBoxSummaryUseCase(
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            CashBoxMapper cashBoxMapper
    ) {
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.cashBoxMapper = cashBoxMapper;
    }

    public CashBoxDetailResponse execute(Long cashBoxId) {
        CashBoxEntity cashBoxEntity = jpaCashBoxRepository.findById(cashBoxId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CAJA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la caja solicitada."
                ));

        List<CashMovementEntity> movements = jpaCashMovementRepository.findAllByCashBoxIdOrderByOccurredAtAsc(cashBoxId);
        return cashBoxMapper.toDetailResponse(cashBoxEntity, movements);
    }
}
