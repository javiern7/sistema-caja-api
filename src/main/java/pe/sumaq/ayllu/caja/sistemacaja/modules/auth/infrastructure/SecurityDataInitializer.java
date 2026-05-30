package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaPermissionRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.PermissionEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;

@Configuration
public class SecurityDataInitializer {

    @Bean
    @Order(10)
    @ConditionalOnProperty(prefix = "app.security.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
    ApplicationRunner seedSecurityData(
            JpaPermissionRepository permissionRepository,
            JpaRoleRepository roleRepository,
            JpaUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Map<String, String> permissions = Map.ofEntries(
                    Map.entry("usuario.gestionar", "Gestion de usuarios"),
                    Map.entry("rol.gestionar", "Gestion de roles y permisos"),
                    Map.entry("negocioevento.gestionar", "Gestion de negocios y eventos"),
                    Map.entry("producto.gestionar", "Gestion de productos"),
                    Map.entry("proveedor.gestionar", "Gestion de proveedores"),
                    Map.entry("venta.registrar", "Registro de ventas"),
                    Map.entry("venta.anular", "Anulacion de ventas"),
                    Map.entry("compra.registrar", "Registro de compras"),
                    Map.entry("egreso.registrar", "Registro de egresos"),
                    Map.entry("caja.abrir", "Apertura de caja"),
                    Map.entry("caja.cerrar", "Cierre de caja"),
                    Map.entry("stock.consultar", "Consulta de stock"),
                    Map.entry("auditoria.consultar", "Consulta de auditoria"),
                    Map.entry("reporte.ver", "Consulta de reportes"),
                    Map.entry("reporte.exportar", "Exportacion de reportes"),
                    Map.entry("reporte.ventas", "Reporte de ventas"),
                    Map.entry("reporte.caja", "Reporte de caja"),
                    Map.entry("reporte.compras", "Reporte de compras"),
                    Map.entry("reporte.egresos", "Reporte de egresos"),
                    Map.entry("reporte.stock", "Reporte de stock"),
                    Map.entry("reporte.utilidad", "Reporte de utilidad")
            );

            permissions.forEach((code, description) -> {
                if (permissionRepository.existsById(code)) {
                    return;
                }
                PermissionEntity permissionEntity = new PermissionEntity();
                permissionEntity.setCode(code);
                permissionEntity.setDescription(description);
                permissionRepository.save(permissionEntity);
            });

            Map<String, RoleSeed> roleSeeds = new LinkedHashMap<>();
            roleSeeds.put("ADMIN", new RoleSeed("Rol administrador", permissionRepository.findAll()));
            roleSeeds.put("CAJERO", new RoleSeed(
                    "Rol cajero",
                    permissionRepository.findByCodeIn(List.of(
                            "venta.registrar",
                            "egreso.registrar",
                            "caja.abrir",
                            "caja.cerrar"
                    ))
            ));
            roleSeeds.put("SUPERVISOR", new RoleSeed(
                    "Supervision operativa de caja, ventas y seguimiento",
                    permissionRepository.findByCodeIn(List.of(
                            "caja.abrir",
                            "caja.cerrar",
                            "venta.registrar",
                            "venta.anular",
                            "egreso.registrar",
                            "stock.consultar",
                            "reporte.ver",
                            "reporte.ventas",
                            "reporte.caja",
                            "reporte.egresos"
                    ))
            ));
            roleSeeds.put("COMPRAS", new RoleSeed(
                    "Operacion de compras, proveedores y seguimiento de stock",
                    permissionRepository.findByCodeIn(List.of(
                            "compra.registrar",
                            "proveedor.gestionar",
                            "stock.consultar",
                            "reporte.compras",
                            "reporte.stock"
                    ))
            ));
            roleSeeds.put("REPORTES", new RoleSeed(
                    "Consulta y exportacion de reportes con auditoria",
                    permissionRepository.findByCodeIn(List.of(
                            "auditoria.consultar",
                            "reporte.ver",
                            "reporte.exportar",
                            "reporte.ventas",
                            "reporte.caja",
                            "reporte.compras",
                            "reporte.egresos",
                            "reporte.stock",
                            "reporte.utilidad"
                    ))
            ));

            Map<String, RoleEntity> seededRoles = new LinkedHashMap<>();
            roleSeeds.forEach((roleName, roleSeed) -> {
                RoleEntity roleEntity = roleRepository.findByName(roleName)
                        .orElseGet(() -> createRole(roleRepository, roleName, roleSeed.description(), roleSeed.permissions()));
                syncRolePermissions(roleRepository, roleEntity, roleSeed.description(), roleSeed.permissions());
                seededRoles.put(roleName, roleEntity);
            });

            createUserIfMissing(userRepository, passwordEncoder, seededRoles.get("ADMIN"), "admin", "Admin123*");
            createUserIfMissing(userRepository, passwordEncoder, seededRoles.get("CAJERO"), "cajero", "Cajero123*");
            createUserIfMissing(userRepository, passwordEncoder, seededRoles.get("SUPERVISOR"), "supervisor", "Supervisor123*");
            createUserIfMissing(userRepository, passwordEncoder, seededRoles.get("COMPRAS"), "compras", "Compras123*");
            createUserIfMissing(userRepository, passwordEncoder, seededRoles.get("REPORTES"), "reportes", "Reportes123*");
        };
    }

    private static RoleEntity createRole(
            JpaRoleRepository roleRepository,
            String name,
            String description,
            List<PermissionEntity> permissions
    ) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(name);
        roleEntity.setDescription(description);
        roleEntity.setPermissions(new LinkedHashSet<>(permissions));
        return roleRepository.save(roleEntity);
    }

    private static void syncRolePermissions(
            JpaRoleRepository roleRepository,
            RoleEntity roleEntity,
            String description,
            List<PermissionEntity> permissions
    ) {
        boolean changed = false;

        if (!java.util.Objects.equals(roleEntity.getDescription(), description)) {
            roleEntity.setDescription(description);
            changed = true;
        }

        Set<String> currentCodes = roleEntity.getPermissions().stream()
                .map(PermissionEntity::getCode)
                .collect(java.util.stream.Collectors.toSet());

        for (PermissionEntity permission : permissions) {
            if (currentCodes.add(permission.getCode())) {
                roleEntity.getPermissions().add(permission);
                changed = true;
            }
        }

        if (changed) {
            roleRepository.save(roleEntity);
        }
    }

    private static void createUserIfMissing(
            JpaUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleEntity roleEntity,
            String username,
            String rawPassword
    ) {
        if (userRepository.findByUsername(username).isEmpty()) {
            userRepository.save(createUser(username, rawPassword, true, roleEntity, passwordEncoder));
        }
    }

    private static UserEntity createUser(
            String username,
            String rawPassword,
            boolean active,
            RoleEntity roleEntity,
            PasswordEncoder passwordEncoder
    ) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPasswordHash(passwordEncoder.encode(rawPassword));
        userEntity.setActive(active);
        userEntity.setRole(roleEntity);
        return userEntity;
    }

    private record RoleSeed(
            String description,
            List<PermissionEntity> permissions
    ) {
    }
}
