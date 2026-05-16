package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application;

import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.CreateProviderRequest;

@Service
public class CreateProviderUseCase {

    private final JpaProviderRepository jpaProviderRepository;

    public CreateProviderUseCase(JpaProviderRepository jpaProviderRepository) {
        this.jpaProviderRepository = jpaProviderRepository;
    }

    public ProviderEntity execute(CreateProviderRequest request) {
        ProviderEntity providerEntity = new ProviderEntity();
        providerEntity.setName(request.name());
        providerEntity.setDocumentNumber(request.documentNumber());
        providerEntity.setContactName(request.contactName());
        providerEntity.setPhone(request.phone());
        providerEntity.setEmail(request.email());
        providerEntity.setActive(request.active());
        return jpaProviderRepository.save(providerEntity);
    }
}
