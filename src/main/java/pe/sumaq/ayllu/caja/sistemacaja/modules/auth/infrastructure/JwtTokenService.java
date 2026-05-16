package pe.sumaq.ayllu.caja.sistemacaja.modules.auth.infrastructure;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import pe.sumaq.ayllu.caja.sistemacaja.config.SecurityProperties;
import pe.sumaq.ayllu.caja.sistemacaja.modules.auth.domain.AuthenticatedUser;

@Service
public class JwtTokenService {

    private final SecurityProperties securityProperties;
    private final SecretKey secretKey;

    public JwtTokenService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                securityProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(AuthenticatedUser authenticatedUser) {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plus(securityProperties.getJwt().getExpiration());

        return Jwts.builder()
                .subject(authenticatedUser.username())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .claim("userId", authenticatedUser.id())
                .claim("role", authenticatedUser.role())
                .claim("active", authenticatedUser.active())
                .claim("permissions", authenticatedUser.permissions())
                .signWith(secretKey)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public SecurityUserPrincipal parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long userId = claims.get("userId", Number.class).longValue();
        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        Boolean active = claims.get("active", Boolean.class);
        List<String> permissions = claims.get("permissions", List.class);

        return SecurityUserPrincipal.authenticated(
                userId,
                username,
                role,
                Boolean.TRUE.equals(active),
                permissions
        );
    }
}
