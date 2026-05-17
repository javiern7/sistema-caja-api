package pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure.SecurityUserPrincipal;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashBoxStatus;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.domain.CashMovementType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashBoxEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.CashMovementEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CashBoxDetailResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.CloseCashBoxRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class CloseCashBoxUseCase {

    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final JpaUserRepository jpaUserRepository;
    private final CashBoxMapper cashBoxMapper;

    public CloseCashBoxUseCase(
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            JpaUserRepository jpaUserRepository,
            CashBoxMapper cashBoxMapper
    ) {
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.cashBoxMapper = cashBoxMapper;
    }

    @Transactional
    public CashBoxDetailResponse execute(
            Long cashBoxId,
            SecurityUserPrincipal principal,
            CloseCashBoxRequest request
    ) {
        CashBoxEntity cashBoxEntity = jpaCashBoxRepository.findById(cashBoxId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CAJA_NO_ENCONTRADA,
                        HttpStatus.NOT_FOUND,
                        "No se encontro la caja solicitada."
                ));

        if (!cashBoxEntity.getStatus().isOpen()) {
            throw new BusinessException(
                    ErrorCode.CAJA_NO_OPERATIVA,
                    HttpStatus.CONFLICT,
                    "La caja no se encuentra operativa para cierre."
            );
        }

        List<CashMovementEntity> movements = jpaCashMovementRepository.findAllByCashBoxIdOrderByOccurredAtAsc(cashBoxId);
        BigDecimal totalSales = sumByType(movements, CashMovementType.VENTA, CashMovementType.ANULACION_VENTA);
        BigDecimal additionalIncome = sumByType(movements, CashMovementType.INGRESO_AJUSTE);
        BigDecimal totalExpenses = sumByType(movements, CashMovementType.EGRESO);
        BigDecimal expectedAmount = cashBoxEntity.getOpeningAmount()
                .add(totalSales)
                .add(additionalIncome)
                .subtract(totalExpenses);
        BigDecimal differenceAmount = request.countedAmount().subtract(expectedAmount);

        if (differenceAmount.compareTo(BigDecimal.ZERO) != 0
                && (request.observation() == null || request.observation().isBlank())) {
            throw new BusinessException(
                    ErrorCode.CAJA_CIERRE_REQUIERE_OBSERVACION,
                    HttpStatus.BAD_REQUEST,
                    "La observacion es obligatoria cuando existe diferencia en el cierre."
            );
        }

        UserEntity closedBy = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        cashBoxEntity.setExpectedAmount(expectedAmount);
        cashBoxEntity.setCountedAmount(request.countedAmount());
        cashBoxEntity.setDifferenceAmount(differenceAmount);
        cashBoxEntity.setClosingObservation(request.observation());
        cashBoxEntity.setClosedAt(LocalDateTime.now());
        cashBoxEntity.setClosedBy(closedBy);
        cashBoxEntity.setStatus(CashBoxStatus.CERRADA);
        CashBoxEntity savedCashBox = jpaCashBoxRepository.save(cashBoxEntity);

        CashMovementEntity movementEntity = new CashMovementEntity();
        movementEntity.setCashBox(savedCashBox);
        movementEntity.setMovementType(CashMovementType.CIERRE);
        movementEntity.setAmount(request.countedAmount());
        movementEntity.setReferenceType("CAJA");
        movementEntity.setReferenceId(savedCashBox.getId().toString());
        movementEntity.setPerformedBy(principal.getUsername());
        movementEntity.setOccurredAt(LocalDateTime.now());
        movementEntity.setObservation(request.observation());
        jpaCashMovementRepository.save(movementEntity);
        movements.add(movementEntity);

        return cashBoxMapper.toDetailResponse(savedCashBox, movements);
    }

    private BigDecimal sumByType(List<CashMovementEntity> movements, CashMovementType... movementTypes) {
        List<CashMovementType> typeList = List.of(movementTypes);

        return movements.stream()
                .filter(movement -> typeList.contains(movement.getMovementType()))
                .map(CashMovementEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
