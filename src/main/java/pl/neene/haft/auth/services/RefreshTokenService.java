package pl.neene.haft.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.neene.haft.auth.exceptions.RefreshTokenDoesNotExistException;
import pl.neene.haft.auth.exceptions.RefreshTokenExpiredException;
import pl.neene.haft.auth.exceptions.RevokedTokenWasUsedException;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.model.RefreshToken;
import pl.neene.haft.auth.model.TokenStatus;
import pl.neene.haft.auth.records.RefreshTokenResult;
import pl.neene.haft.auth.repositories.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${app.refresh-token-validity}")
    private Duration refreshTokenValidity;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshTokenResult generateRefreshToken(NeeNeUser user) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String hashedToken = hashRefreshToken(refreshToken);

        RefreshToken token = new RefreshToken();
        token.setToken(hashedToken);
        token.setUser(user);
        token.setTokenStatus(TokenStatus.ACTIVE);
        token.setExpiresAt(Instant.now().plus(refreshTokenValidity));

        refreshTokenRepository.save(token);

        return new RefreshTokenResult(user, refreshToken, refreshTokenValidity);
    }

    @Transactional
    public RefreshTokenResult refresh(String rawToken){
        RefreshToken refreshToken = findRefreshTokenByTokenOrThrow(rawToken);

        NeeNeUser user = refreshToken.getUser();

        if (refreshToken.getTokenStatus() == TokenStatus.REVOKED) {
            revokedRefreshTokenUsedProcedure(user);
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            tokenExpiredProcedure(refreshToken);
        }

        RefreshTokenResult result = generateRefreshToken(user);
        refreshToken.setTokenStatus(TokenStatus.REVOKED);
        refreshTokenRepository.save(refreshToken);

        return result;
    }

    public void revokeToken(String refreshToken) {
        RefreshToken refreshTokenEntity = findRefreshTokenByTokenOrThrow(refreshToken);
        refreshTokenEntity.setTokenStatus(TokenStatus.REVOKED);
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("Refresh token revoked: {}", refreshTokenEntity.getId());
    }

    private String hashRefreshToken(String refreshToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is somehow not available ",e);
        }
    }

    private RefreshToken findRefreshTokenByTokenOrThrow(String rawToken){
        String hashedToken = hashRefreshToken(rawToken);
        return refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(()-> {
                    log.warn("Not existing refresh token was used for refreshing: {}", rawToken);
                    return new RefreshTokenDoesNotExistException("Such refresh token does not exist");
                });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void revokedRefreshTokenUsedProcedure(NeeNeUser user){
        log.warn("Revoked refresh token was used! Revoking all tokens for user: {}", user.getId());
        int i = refreshTokenRepository.setRevokedByUser(user);
        log.warn("Revoked tokens count: {}", i);
        throw new RevokedTokenWasUsedException("Revoked refresh token was used!");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void tokenExpiredProcedure(RefreshToken refreshToken){
        refreshToken.setTokenStatus(TokenStatus.REVOKED);
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token expired: {}", refreshToken.getId());
        throw new RefreshTokenExpiredException("Refresh token expired");
    }
}
