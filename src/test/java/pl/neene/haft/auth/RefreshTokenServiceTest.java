package pl.neene.haft.auth;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import pl.neene.haft.auth.exceptions.RefreshTokenDoesNotExistException;
import pl.neene.haft.auth.exceptions.RefreshTokenExpiredException;
import pl.neene.haft.auth.exceptions.RevokedTokenWasUsedException;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.model.RefreshToken;
import pl.neene.haft.auth.model.TokenStatus;
import pl.neene.haft.auth.records.RefreshTokenResult;
import pl.neene.haft.auth.repositories.RefreshTokenRepository;
import pl.neene.haft.auth.services.RefreshTokenService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    RefreshTokenRepository refreshTokenRepository;
    Duration refreshTokenValidity;
    RefreshTokenService refreshTokenService;
    TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = mock(TransactionTemplate.class);
        refreshTokenValidity = Duration.ofDays(30);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        refreshTokenService = new RefreshTokenService(transactionTemplate, refreshTokenValidity, refreshTokenRepository);
    }

    @Nested
    class GenerateRefreshToken {
        NeeNeUser user;
        @BeforeEach
        void setUp() {
            user = new NeeNeUser();
            user.setEmail("foobar@mail.com");
            user.setFirstName("Foo");
            user.setLastName("Bar");
        }

        @Test
        void validRequest_returnsRefreshTokenResult() {
            RefreshTokenResult result = refreshTokenService.generateRefreshToken(user);
            assertEquals(user, result.user());
            assertNotNull(result.refreshToken());
            assertEquals(refreshTokenValidity, result.expiresIn());
            verify(refreshTokenRepository,times(1)).save(any());
        }
    }

    @Nested
    class RefreshTokenFunctionality {
        String rawToken;
        String hashedToken;
        NeeNeUser user;
        @BeforeEach
        void setUp() throws NoSuchAlgorithmException {
            user = new NeeNeUser();
            user.setEmail("foobar@mail.com");
            user.setFirstName("Foo");
            user.setLastName("Bar");


            rawToken = "rawTokenExample";
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            hashedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        }

        @Test
        void validRequest_returnsRefreshTokenResult() throws NoSuchAlgorithmException {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(hashedToken);
            refreshToken.setUser(user);
            refreshToken.setTokenStatus(TokenStatus.ACTIVE);
            refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(30)));

            when(refreshTokenRepository.findByToken(hashedToken)).thenReturn(Optional.of(refreshToken));

            RefreshTokenResult result = refreshTokenService.refresh(rawToken);
            assertEquals(user, result.user());
            assertNotNull(result.refreshToken());
            verify(refreshTokenRepository,times(2)).save(any());
        }

        @Test
        void nonExistingToken_throwsRefreshTokenDoesNotExistException(){
            when(refreshTokenRepository.findByToken(hashedToken)).thenReturn(Optional.empty());
            assertThrows(RefreshTokenDoesNotExistException.class, () -> refreshTokenService.refresh(rawToken));
        }

        @Test void expiredToken_throwsRefreshTokenExpiredException(){
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(hashedToken);
            refreshToken.setUser(user);
            refreshToken.setTokenStatus(TokenStatus.ACTIVE);
            refreshToken.setExpiresAt(Instant.now().minus(Duration.ofDays(30)));

            when(refreshTokenRepository.findByToken(hashedToken)).thenReturn(Optional.of(refreshToken));
            when(transactionTemplate.execute(any(TransactionCallback.class)))
            .thenAnswer(invocation -> {
                TransactionCallback<?> callback = invocation.getArgument(0);
                return callback.doInTransaction(new SimpleTransactionStatus());
            });

            assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.refresh(rawToken));
            verify(transactionTemplate, times(1)).execute(any());
        }

        @Test void revokedToken_throwsRevokedTokenWasUsedException(){
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(hashedToken);
            refreshToken.setUser(user);
            refreshToken.setTokenStatus(TokenStatus.REVOKED);
            refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(30)));

            when(refreshTokenRepository.findByToken(hashedToken)).thenReturn(Optional.of(refreshToken));
              when(transactionTemplate.execute(any(TransactionCallback.class)))
            .thenAnswer(invocation -> {
                TransactionCallback<?> callback = invocation.getArgument(0);
                return callback.doInTransaction(new SimpleTransactionStatus());
            });

            assertThrows(RevokedTokenWasUsedException.class, () -> refreshTokenService.refresh(rawToken));
            verify(transactionTemplate, times(1)).execute(any());
        }
    }

    @Nested
    class revokeToken {
        String rawToken;
        String hashedToken;

        @BeforeEach
        void setUp() throws NoSuchAlgorithmException {
            rawToken = "rawTokenExample";

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            hashedToken = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        }

        @Test
        void validRequest_revokesToken(){
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(hashedToken);
            refreshToken.setUser(new NeeNeUser());
            refreshToken.setTokenStatus(TokenStatus.ACTIVE);
            refreshToken.setExpiresAt(Instant.now().plus(Duration.ofDays(30)));
            when(refreshTokenRepository.findByToken(hashedToken)).thenReturn(Optional.of(refreshToken));

            refreshTokenService.revokeToken(rawToken);
            refreshToken.setTokenStatus(TokenStatus.REVOKED);
            verify(refreshTokenRepository, times(1)).save(refreshToken);
        }

        @Test
        void nonExistingToken_throwsRefreshTokenDoesNotExistException(){
            when(refreshTokenRepository.findByToken(hashedToken)).thenReturn(Optional.empty());
            assertThrows(RefreshTokenDoesNotExistException.class, () -> refreshTokenService.revokeToken(rawToken));
        }
    }

}
