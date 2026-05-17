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
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.presentation.dto.OpenCashBoxRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class OpenCashBoxUseCase {

    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final JpaOperationalContextRepository jpaOperationalContextRepository;
    private final JpaUserRepository jpaUserRepository;
    private final CashBoxMapper cashBoxMapper;

    public OpenCashBoxUseCase(
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            JpaOperationalContextRepository jpaOperationalContextRepository,
            JpaUserRepository jpaUserRepository,
            CashBoxMapper cashBoxMapper
    ) {
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.jpaOperationalContextRepository = jpaOperationalContextRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.cashBoxMapper = cashBoxMapper;
    }

    @Transactional
    public CashBoxDetailResponse execute(SecurityUserPrincipal principal, OpenCashBoxRequest request) {
        if (jpaCashBoxRepository.existsByOpenedByIdAndStatus(principal.toAuthenticatedUser().id(), CashBoxStatus.ABIERTA)) {
            throw new BusinessException(
                    ErrorCode.CAJA_USUARIO_YA_TIENE_CAJA_ABIERTA,
                    HttpStatus.CONFLICT,
                    "El usuario ya tiene una caja abierta."
            );
        }

        if (jpaCashBoxRepository.existsByOperationalContextIdAndStatus(
                request.operationalContextId(),
                CashBoxStatus.ABIERTA
        )) {
            throw new BusinessException(
                    ErrorCode.CAJA_CONTEXTO_YA_TIENE_CAJA_ABIERTA,
                    HttpStatus.CONFLICT,
                    "El contexto operativo ya tiene una caja abierta."
            );
        }

        OperationalContextEntity operationalContext = jpaOperationalContextRepository.findById(request.operationalContextId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NEGOCIO_EVENTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el contexto operativo solicitado."
                ));

        if (!operationalContext.getStatus().isOperable()) {
            throw new BusinessException(
                    ErrorCode.CAJA_CONTEXTO_NO_OPERABLE,
                    HttpStatus.CONFLICT,
                    "El contexto operativo no se encuentra disponible para abrir caja."
            );
        }

        UserEntity userEntity = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        CashBoxEntity cashBoxEntity = new CashBoxEntity();
        cashBoxEntity.setOperationalContext(operationalContext);
        cashBoxEntity.setOpenedBy(userEntity);
        cashBoxEntity.setStatus(CashBoxStatus.ABIERTA);
        cashBoxEntity.setOpeningAmount(request.openingAmount());
        cashBoxEntity.setOpeningObservation(request.observation());
        cashBoxEntity.setOpenedAt(LocalDateTime.now());
        cashBoxEntity.setExpectedAmount(request.openingAmount());
        CashBoxEntity savedCashBox = jpaCashBoxRepository.save(cashBoxEntity);

        CashMovementEntity movementEntity = new CashMovementEntity();
        movementEntity.setCashBox(savedCashBox);
        movementEntity.setMovementType(CashMovementType.APERTURA);
        movementEntity.setAmount(request.openingAmount() != null ? request.openingAmount() : BigDecimal.ZERO);
        movementEntity.setReferenceType("CAJA");
        movementEntity.setReferenceId(savedCashBox.getId().toString());
        movementEntity.setPerformedBy(principal.getUsername());
        movementEntity.setOccurredAt(LocalDateTime.now());
        movementEntity.setObservation(request.observation());
        jpaCashMovementRepository.save(movementEntity);

        return cashBoxMapper.toDetailResponse(
                savedCashBox,
                List.of(movementEntity)
        );
    }
}
