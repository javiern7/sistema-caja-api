package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application;

import java.util.List;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;

@Service
public class ListProvidersUseCase {

    private final JpaProviderRepository jpaProviderRepository;

    public ListProvidersUseCase(JpaProviderRepository jpaProviderRepository) {
        this.jpaProviderRepository = jpaProviderRepository;
    }

    public List<ProviderEntity> execute() {
        return jpaProviderRepository.findAll();
    }
}
