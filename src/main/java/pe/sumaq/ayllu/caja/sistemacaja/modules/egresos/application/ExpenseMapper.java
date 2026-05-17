package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.ExpenseEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.presentation.dto.ExpenseResponse;

@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(ExpenseEntity expenseEntity) {
        return new ExpenseResponse(
                expenseEntity.getId(),
                expenseEntity.getOperationalContext().getId(),
                expenseEntity.getOperationalContext().getName(),
                expenseEntity.getCashBox() != null ? expenseEntity.getCashBox().getId() : null,
                expenseEntity.getExpenseType(),
                expenseEntity.getCategory(),
                expenseEntity.getDescription(),
                expenseEntity.getPaymentMethod(),
                expenseEntity.getAmount(),
                expenseEntity.getResponsible(),
                expenseEntity.getObservation(),
                expenseEntity.getRecordedBy().getUsername(),
                expenseEntity.getExpenseDate(),
                expenseEntity.getCreatedAt()
        );
    }
}
