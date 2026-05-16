package pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.presentation.dto;

public record ProviderResponse(
        Long id,
        String name,
        String documentNumber,
        String contactName,
        String phone,
        String email,
        boolean active
) {
}
