package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application;

import java.util.List;

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
    public List<ExpenseResponse> execute() {
        return jpaExpenseRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(expenseMapper::toResponse)
                .toList();
    }
}
