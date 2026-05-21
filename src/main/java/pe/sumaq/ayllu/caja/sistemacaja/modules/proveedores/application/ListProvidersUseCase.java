package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;

@Service
public class ListProvidersUseCase {

    private final JpaProviderRepository jpaProviderRepository;

    public ListProvidersUseCase(JpaProviderRepository jpaProviderRepository) {
        this.jpaProviderRepository = jpaProviderRepository;
    }

    public Page<ProviderEntity> execute(Pageable pageable) {
        return jpaProviderRepository.findAll(pageable);
    }
}
