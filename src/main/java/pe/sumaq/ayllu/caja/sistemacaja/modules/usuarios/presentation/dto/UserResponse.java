package pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.presentation.dto;

public record UserResponse(
        Long id,
        String username,
        boolean active,
        Long roleId,
        String roleName
) {
}
