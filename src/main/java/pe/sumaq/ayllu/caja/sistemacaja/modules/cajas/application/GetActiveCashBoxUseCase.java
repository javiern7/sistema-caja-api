package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;

@Service
public class GetActiveCashBoxUseCase {

    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final CashBoxMapper cashBoxMapper;

    public GetActiveCashBoxUseCase(
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            CashBoxMapper cashBoxMapper
    ) {
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.cashBoxMapper = cashBoxMapper;
    }

    public CashBoxDetailResponse execute(SecurityUserPrincipal principal) {
        CashBoxEntity cashBoxEntity = jpaCashBoxRepository.findFirstByOpenedByIdAndStatusOrderByOpenedAtDesc(
                        principal.toAuthenticatedUser().id(),
                        CashBoxStatus.ABIERTA
                )
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CAJA_ACTIVA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "El usuario no tiene una caja activa."
                ));

        List<CashMovementEntity> movements = jpaCashMovementRepository.findAllByCashBoxIdOrderByOccurredAtAsc(cashBoxEntity.getId());
        return cashBoxMapper.toDetailResponse(cashBoxEntity, movements);
    }
}
