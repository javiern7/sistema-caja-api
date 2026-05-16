package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.config.SecurityProperties;

@Service
public class BootstrapUserDetailsService implements UserDetailsService {

    private final Map<String, SecurityUserPrincipal> usersByUsername;

    public BootstrapUserDetailsService(SecurityProperties securityProperties, PasswordEncoder passwordEncoder) {
        this.usersByUsername = new LinkedHashMap<>();
        securityProperties.getBootstrapUsers().forEach(user -> usersByUsername.put(
                user.getUsername(),
                new SecurityUserPrincipal(
                        user.getId(),
                        user.getUsername(),
                        passwordEncoder.encode(user.getPassword()),
                        user.getRole(),
                        user.isActive(),
                        user.getPermissions()
                )
        ));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(usersByUsername.get(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
