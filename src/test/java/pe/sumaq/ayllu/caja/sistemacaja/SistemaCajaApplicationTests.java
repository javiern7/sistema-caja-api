package pe.sumaq.ayllu.caja.sistemacaja;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import pe.sumaq.ayllu.caja.sistemacaja.modules.negocioseventos.infrastructure.persistence.JpaOperationalContextRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashBoxRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.cajas.infrastructure.persistence.JpaCashMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auditoria.infrastructure.persistence.JpaAuditOperationRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.compras.infrastructure.persistence.JpaPurchaseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.egresos.infrastructure.persistence.JpaExpenseRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.productos.infrastructure.persistence.JpaProductRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.proveedores.infrastructure.persistence.JpaProviderRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaPermissionRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.JpaRoleRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.PermissionEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.rolespermisos.infrastructure.persistence.RoleEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockCurrentRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.stock.infrastructure.persistence.JpaStockMovementRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.JpaUserRepository;
import pe.sumaq.ayllu.caja.sistemacaja.modules.usuarios.infrastructure.persistence.UserEntity;
import pe.sumaq.ayllu.caja.sistemacaja.modules.ventas.infrastructure.persistence.JpaSaleRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
        "app.security.seed.enabled=false"
})
@AutoConfigureMockMvc
class SistemaCajaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaOperationalContextRepository jpaOperationalContextRepository;

    @MockBean
    private JpaUserRepository jpaUserRepository;

    @MockBean
    private JpaRoleRepository jpaRoleRepository;

    @MockBean
    private JpaPermissionRepository jpaPermissionRepository;

    @MockBean
    private JpaProductRepository jpaProductRepository;

    @MockBean
    private JpaProviderRepository jpaProviderRepository;

    @MockBean
    private JpaStockCurrentRepository jpaStockCurrentRepository;

    @MockBean
    private JpaStockMovementRepository jpaStockMovementRepository;

    @MockBean
    private JpaCashBoxRepository jpaCashBoxRepository;

    @MockBean
    private JpaCashMovementRepository jpaCashMovementRepository;

    @MockBean
    private JpaSaleRepository jpaSaleRepository;

    @MockBean
    private JpaPurchaseRepository jpaPurchaseRepository;

    @MockBean
    private JpaExpenseRepository jpaExpenseRepository;

    @MockBean
    private JpaAuditOperationRepository jpaAuditOperationRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void healthEndpointShouldBePublic() throws Exception {
        mockMvc.perform(get("/api/v1/system/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void meEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void operationalContextsEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/contextos-operativos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void stockEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/stock"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void activeCashBoxEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/cajas/activa"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void saleDetailEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void purchaseDetailEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/compras/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void expenseDetailEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/egresos/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void auditOperationsEndpointShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/auditoria/operaciones"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_INVALID_TOKEN"));
    }

    @Test
    void loginShouldReturnTokenForAdminFromPersistentUserStore() throws Exception {
        when(jpaUserRepository.findByUsername("admin")).thenReturn(Optional.of(buildAdminUser()));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin123*"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.permissions").isArray());
    }

    private UserEntity buildAdminUser() {
        PermissionEntity usersPermission = new PermissionEntity();
        usersPermission.setCode("usuario.gestionar");
        usersPermission.setDescription("Gestion de usuarios");

        PermissionEntity rolesPermission = new PermissionEntity();
        rolesPermission.setCode("rol.gestionar");
        rolesPermission.setDescription("Gestion de roles");

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1L);
        roleEntity.setName("ADMIN");
        roleEntity.setDescription("Administrador");
        roleEntity.setPermissions(new LinkedHashSet<>(List.of(usersPermission, rolesPermission)));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("admin");
        userEntity.setPasswordHash(new BCryptPasswordEncoder().encode("Admin123*"));
        userEntity.setActive(true);
        userEntity.setRole(roleEntity);
        return userEntity;
    }
}
