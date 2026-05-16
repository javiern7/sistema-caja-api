package pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContext;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.domain.OperationalContextStatus;

@Component
public class OperationalContextPersistenceAdapter implements OperationalContextRepository {

    private final JpaOperationalContextRepository jpaOperationalContextRepository;

    public OperationalContextPersistenceAdapter(JpaOperationalContextRepository jpaOperationalContextRepository) {
        this.jpaOperationalContextRepository = jpaOperationalContextRepository;
    }

    @Override
    public List<OperationalContext> findAvailableForOperation() {
        return jpaOperationalContextRepository.findAllByStatusOrderByStartDateAsc(OperationalContextStatus.EN_CURSO)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<OperationalContext> findAll() {
        return jpaOperationalContextRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<OperationalContext> findById(Long id) {
        return jpaOperationalContextRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public OperationalContext save(OperationalContext operationalContext) {
        OperationalContextEntity entity = toEntity(operationalContext);
        return toDomain(jpaOperationalContextRepository.save(entity));
    }

    private OperationalContext toDomain(OperationalContextEntity entity) {
        return new OperationalContext(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getType(),
                entity.getStatus(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getDescription()
        );
    }

    private OperationalContextEntity toEntity(OperationalContext operationalContext) {
        OperationalContextEntity entity = new OperationalContextEntity();
        entity.setId(operationalContext.id());
        entity.setCode(operationalContext.code());
        entity.setName(operationalContext.name());
        entity.setType(operationalContext.type());
        entity.setStatus(operationalContext.status());
        entity.setStartDate(operationalContext.startDate());
        entity.setEndDate(operationalContext.endDate());
        entity.setDescription(operationalContext.description());
        return entity;
    }
}
