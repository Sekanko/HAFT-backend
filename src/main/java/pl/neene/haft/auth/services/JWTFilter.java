package pl.neene.haft.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.neene.haft.auth.model.SystemRole;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header.isBlank() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtService.extractClaims(token);
            UUID subject = UUID.fromString(claims.getSubject());
            SystemRole systemRole = SystemRole.valueOf(claims.get("sys_role").toString());
            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(systemRole.name()));
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(subject, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (JwtException e) {
            logger.info("JWT token is invalid:" + e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
