package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;

@Service
public class ListExpensesUseCase {

    private final JpaExpenseRepository jpaExpenseRepository;
    private final ExpenseMapper expenseMapper;

    public ListExpensesUseCase(JpaExpenseRepository jpaExpenseRepository, ExpenseMapper expenseMapper) {
        this.jpaExpenseRepository = jpaExpenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> execute(Long operationalContextId, Pageable pageable) {
        if (operationalContextId != null) {
            return jpaExpenseRepository.findAllByOperationalContextIdOrderByCreatedAtDesc(operationalContextId, pageable)
                    .map(expenseMapper::toResponse);
        }

        return jpaExpenseRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(expenseMapper::toResponse);
    }
}
