package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application;

import java.time.LocalDateTime;

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
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.domain.ExpenseType;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.ExpenseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.CreateExpenseRequest;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.OperationalContextEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Service
public class CreateExpenseUseCase {

    private final JpaExpenseRepository jpaExpenseRepository;
    private final JpaOperationalContextRepository jpaOperationalContextRepository;
    private final JpaCashBoxRepository jpaCashBoxRepository;
    private final JpaCashMovementRepository jpaCashMovementRepository;
    private final JpaUserRepository jpaUserRepository;
    private final ExpenseMapper expenseMapper;

    public CreateExpenseUseCase(
            JpaExpenseRepository jpaExpenseRepository,
            JpaOperationalContextRepository jpaOperationalContextRepository,
            JpaCashBoxRepository jpaCashBoxRepository,
            JpaCashMovementRepository jpaCashMovementRepository,
            JpaUserRepository jpaUserRepository,
            ExpenseMapper expenseMapper
    ) {
        this.jpaExpenseRepository = jpaExpenseRepository;
        this.jpaOperationalContextRepository = jpaOperationalContextRepository;
        this.jpaCashBoxRepository = jpaCashBoxRepository;
        this.jpaCashMovementRepository = jpaCashMovementRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.expenseMapper = expenseMapper;
    }

    @Transactional
    public ExpenseResponse execute(SecurityUserPrincipal principal, CreateExpenseRequest request) {
        OperationalContextEntity operationalContext = jpaOperationalContextRepository.findById(request.operationalContextId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NEGOCIO_EVENTO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el contexto operativo solicitado."
                ));

        UserEntity recordedBy = jpaUserRepository.findById(principal.toAuthenticatedUser().id())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USUARIO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el usuario autenticado."
                ));

        CashBoxEntity cashBox = null;
        if (request.expenseType() == ExpenseType.CAJA) {
            if (request.cashBoxId() == null) {
                throw new BusinessException(
                        ErrorCode.EGRESO_CAJA_REQUERIDA,
                        HttpStatus.BAD_REQUEST,
                        "El egreso de caja requiere una caja asociada."
                );
            }

            cashBox = jpaCashBoxRepository.findById(request.cashBoxId())
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.CAJA_NO_ENCONTRADA,
                            HttpStatus.NOT_FOUND,
                            "No se encontro la caja solicitada."
                    ));

            if (cashBox.getStatus() != CashBoxStatus.ABIERTA
                    || !cashBox.getOperationalContext().getId().equals(request.operationalContextId())) {
                throw new BusinessException(
                        ErrorCode.EGRESO_TIPO_CAJA_INVALIDO,
                        HttpStatus.CONFLICT,
                        "La caja indicada no es valida para registrar el egreso."
                );
            }
        }

        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setOperationalContext(operationalContext);
        expenseEntity.setCashBox(cashBox);
        expenseEntity.setRecordedBy(recordedBy);
        expenseEntity.setExpenseType(request.expenseType());
        expenseEntity.setCategory(request.category());
        expenseEntity.setDescription(request.description());
        expenseEntity.setPaymentMethod(request.paymentMethod());
        expenseEntity.setAmount(request.amount());
        expenseEntity.setResponsible(request.responsible());
        expenseEntity.setObservation(request.observation());
        expenseEntity.setExpenseDate(request.expenseDate());
        expenseEntity.setCreatedAt(LocalDateTime.now());
        ExpenseEntity savedExpense = jpaExpenseRepository.save(expenseEntity);

        if (cashBox != null) {
            CashMovementEntity cashMovement = new CashMovementEntity();
            cashMovement.setCashBox(cashBox);
            cashMovement.setMovementType(CashMovementType.EGRESO);
            cashMovement.setAmount(request.amount());
            cashMovement.setReferenceType("EGRESO");
            cashMovement.setReferenceId(savedExpense.getId().toString());
            cashMovement.setPerformedBy(principal.getUsername());
            cashMovement.setOccurredAt(LocalDateTime.now());
            cashMovement.setObservation(request.description());
            jpaCashMovementRepository.save(cashMovement);
        }

        return expenseMapper.toResponse(savedExpense);
    }
}
