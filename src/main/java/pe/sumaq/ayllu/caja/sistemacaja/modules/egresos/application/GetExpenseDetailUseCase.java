package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;

@Service
public class GetExpenseDetailUseCase {

    private final JpaExpenseRepository jpaExpenseRepository;
    private final ExpenseMapper expenseMapper;

    public GetExpenseDetailUseCase(JpaExpenseRepository jpaExpenseRepository, ExpenseMapper expenseMapper) {
        this.jpaExpenseRepository = jpaExpenseRepository;
        this.expenseMapper = expenseMapper;
    }

    public ExpenseResponse execute(Long expenseId) {
        return jpaExpenseRepository.findById(expenseId)
                .map(expenseMapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.EGRESO_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el egreso solicitado."
                ));
    }
}
