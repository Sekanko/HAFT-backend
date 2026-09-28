package pl.neene.haft.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.model.NeeNeUser;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service

public class JwtService {
    private final String issuer;
    private final String audience;
    private final Duration expirationTime;
    private final SecretKey signingKey;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-time}") Duration expirationTime,
            @Value("${jwt.audience}") String audience,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationTime = expirationTime;
        this.audience = audience;
        this.issuer = issuer;
    }

    public String generateJWT(NeeNeUser user){
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationTime);
        UUID subject = user.getId();
        Map<String, Object> claims = Map.of(
                "sys_role", user.getSystemRole()
        );

        return Jwts.builder()
                .claims(claims)
                .subject(subject.toString())
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
