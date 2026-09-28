package pl.neene.haft.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.model.SystemRole;
import pl.neene.haft.auth.services.JwtService;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class JwtServiceTest {

    String secret;
    String issuer;
    String audience;
    Duration expirationTime;
    SecretKey signingKey;
    JwtService jwtService;
    String testJwt;
    NeeNeUser user;
    Instant fixedInstant;

    @BeforeEach
    void setUp(){
        secret = "a-string-secret-at-least-256-bits-long";
        issuer = "haft-api";
        audience = "haft-client";
        signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        expirationTime = Duration.ofDays(30);
        fixedInstant = Instant.parse("2200-01-01T00:00:00.00Z");


        jwtService = new JwtService(secret, expirationTime, audience, issuer);
        UUID userID = UUID.fromString("3f24150f-276e-49fc-8bdf-e70a41d19f18");

        user = new NeeNeUser();
        user.setEmail("foobar@mail.com");
        user.setSystemRole(SystemRole.ADMIN);
        user.setId(UUID.fromString(userID.toString()));

        testJwt = "eyJhbGciOiJIUzI1NiJ9.eyJzeXNfcm9sZSI6IkFETUlOIiwic3ViIjoiM2YyNDE1MGYtMjc2ZS00OWZjLThiZGYtZTcwYTQxZDE5ZjE4IiwiaXNzIjoiaGFmdC1hcGkiLCJhdWQiOlsiaGFmdC1jbGllbnQiXSwiaWF0Ijo3MjU4MTE4NDAwLCJleHAiOjcyNjA3MTA0MDB9.1VumWJRhaPFkpKO4IOaDOarS84UfBrx65i2GKCSWLIs";
    }

    @Test
    void generateJWTTest(){
        try (MockedStatic<Instant> mockedStatic = mockStatic(Instant.class)){
            mockedStatic.when(Instant::now).thenReturn(fixedInstant);
            String jwt = jwtService.generateJWT(user);
            assertEquals(testJwt, jwt);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    @Test
    void extractClaimsTest(){
        Claims expectedClaims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(testJwt)
                .getPayload();

        Claims claimsResult = jwtService.extractClaims(testJwt);
        assertEquals(expectedClaims, claimsResult);
    }
}
