package pl.neene.haft.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.model.RefreshToken;
import pl.neene.haft.auth.model.TokenStatus;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.tokenStatus = TokenStatus.REVOKED WHERE rt.user = :user and rt.tokenStatus = TokenStatus.ACTIVE")
    int setRevokedByUser(NeeNeUser user);
}
