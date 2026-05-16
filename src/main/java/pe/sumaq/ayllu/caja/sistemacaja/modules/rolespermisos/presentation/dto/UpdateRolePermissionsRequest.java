package pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.presentation.dto;

import java.util.List;

public record UpdateRolePermissionsRequest(
        List<String> permissions
) {
}
