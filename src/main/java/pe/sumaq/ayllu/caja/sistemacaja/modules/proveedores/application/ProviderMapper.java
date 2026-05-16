package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.application;

import org.springframework.stereotype.Component;

import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.ProviderEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto.ProviderResponse;

@Component
public class ProviderMapper {

    public ProviderResponse toResponse(ProviderEntity providerEntity) {
        return new ProviderResponse(
                providerEntity.getId(),
                providerEntity.getName(),
                providerEntity.getDocumentNumber(),
                providerEntity.getContactName(),
                providerEntity.getPhone(),
                providerEntity.getEmail(),
                providerEntity.isActive()
        );
    }
}
