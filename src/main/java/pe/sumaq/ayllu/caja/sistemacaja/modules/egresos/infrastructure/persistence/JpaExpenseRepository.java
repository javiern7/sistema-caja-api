package pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaExpenseRepository extends JpaRepository<ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {

    List<ExpenseEntity> findAllByOrderByCreatedAtDesc();

    Page<ExpenseEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ExpenseEntity> findAllByOperationalContextIdOrderByCreatedAtDesc(Long operationalContextId, Pageable pageable);
}
