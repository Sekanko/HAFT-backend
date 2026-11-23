package pl.neene.haft.auth.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.neene.haft.auth.model.SystemRole;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFactory {
    private final JwtService jwtService;

    public Optional<Authentication> fromHeader(String header){
        if (header == null || !header.startsWith("Bearer ")) return Optional.empty();

        String token = header.substring(7);

        if (token.isBlank()) return Optional.empty();

        Claims claims = jwtService.extractClaims(token);
        UUID subject = UUID.fromString(claims.getSubject());
        SystemRole systemRole = SystemRole.valueOf(claims.get("sys_role").toString());
        Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(systemRole.name()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(subject, null, authorities);

        return Optional.of(authenticationToken);
    }
}
