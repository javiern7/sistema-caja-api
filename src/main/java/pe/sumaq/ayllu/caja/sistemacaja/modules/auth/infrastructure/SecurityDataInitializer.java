package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @ConditionalOnProperty(prefix = "app.security.seed", name = "enabled", havingValue = "true", matchIfMissing = true)
    ApplicationRunner seedSecurityData(
            JpaPermissionRepository permissionRepository,
            JpaRoleRepository roleRepository,
            JpaUserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

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
                    Map.entry("stock.consultar", "Consulta de stock")
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

            List<PermissionEntity> adminPermissions = permissionRepository.findAll();
            List<PermissionEntity> cajeroPermissions = permissionRepository.findByCodeIn(List.of(
                    "venta.registrar",
                    "egreso.registrar",
                    "caja.abrir",
                    "caja.cerrar"
            ));

            RoleEntity adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> createRole(roleRepository, "ADMIN", "Rol administrador", adminPermissions));
            RoleEntity cajeroRole = roleRepository.findByName("CAJERO")
                    .orElseGet(() -> createRole(roleRepository, "CAJERO", "Rol cajero", cajeroPermissions));

            userRepository.save(createUser("admin", "Admin123*", true, adminRole, passwordEncoder));
            userRepository.save(createUser("cajero", "Cajero123*", true, cajeroRole, passwordEncoder));
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
}
