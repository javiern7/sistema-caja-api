package pe.sumaq.ayllu.caja.sistemacaja.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private final Jwt jwt = new Jwt();
    private List<BootstrapUser> bootstrapUsers = new ArrayList<>();

    public Jwt getJwt() {
        return jwt;
    }

    public List<BootstrapUser> getBootstrapUsers() {
        return bootstrapUsers;
    }

    public void setBootstrapUsers(List<BootstrapUser> bootstrapUsers) {
        this.bootstrapUsers = bootstrapUsers;
    }

    public static class Jwt {
        private String secret;
        private Duration expiration = Duration.ofHours(8);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getExpiration() {
            return expiration;
        }

        public void setExpiration(Duration expiration) {
            this.expiration = expiration;
        }
    }

    public static class BootstrapUser {
        private Long id;
        private String username;
        private String password;
        private String role;
        private boolean active;
        private List<String> permissions = new ArrayList<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
}
