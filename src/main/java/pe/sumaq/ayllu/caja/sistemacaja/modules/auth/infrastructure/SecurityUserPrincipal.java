package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain.AuthenticatedUser;

public class SecurityUserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String role;
    private final boolean active;
    private final List<String> permissions;
    private final List<GrantedAuthority> authorities;

    public SecurityUserPrincipal(
            Long id,
            String username,
            String password,
            String role,
            boolean active,
            List<String> permissions
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
        this.permissions = List.copyOf(permissions);
        this.authorities = buildAuthorities(role, permissions);
    }

    public static SecurityUserPrincipal authenticated(
            Long id,
            String username,
            String role,
            boolean active,
            List<String> permissions
    ) {
        return new SecurityUserPrincipal(id, username, "", role, active, permissions);
    }

    public AuthenticatedUser toAuthenticatedUser() {
        return new AuthenticatedUser(id, username, role, active, permissions);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    private static List<GrantedAuthority> buildAuthorities(String role, List<String> permissions) {
        List<GrantedAuthority> permissionAuthorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();

        return java.util.stream.Stream.concat(
                java.util.stream.Stream.of(new SimpleGrantedAuthority("ROLE_" + role)),
                permissionAuthorities.stream()
        ).toList();
    }
}
