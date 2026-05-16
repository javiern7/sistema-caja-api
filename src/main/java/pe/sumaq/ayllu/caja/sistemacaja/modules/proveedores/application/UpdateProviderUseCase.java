package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.common.exception.BusinessException;
import pe.sumaq.ayllu.caja.sistemacaja.common.exception.ErrorCode;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.UpdateProviderRequest;

@Service
public class UpdateProviderUseCase {

    private final JpaProviderRepository jpaProviderRepository;

    public UpdateProviderUseCase(JpaProviderRepository jpaProviderRepository) {
        this.jpaProviderRepository = jpaProviderRepository;
    }

    public ProviderEntity execute(Long providerId, UpdateProviderRequest request) {
        ProviderEntity providerEntity = jpaProviderRepository.findById(providerId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.PROVEEDOR_NO_ENCONTRADO,
                        HttpStatus.NOT_FOUND,
                        "No se encontro el proveedor solicitado."
                ));

        providerEntity.setName(request.name());
        providerEntity.setDocumentNumber(request.documentNumber());
        providerEntity.setContactName(request.contactName());
        providerEntity.setPhone(request.phone());
        providerEntity.setEmail(request.email());
        providerEntity.setActive(request.active());
        return jpaProviderRepository.save(providerEntity);
    }
}
