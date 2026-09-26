package pl.neene.haft.auth.records;

import pl.neene.haft.auth.model.NeeNeUser;

import java.time.Duration;

public record RefreshTokenResult(
        NeeNeUser user,
        String refreshToken,
        Duration expiresIn
) {
}
