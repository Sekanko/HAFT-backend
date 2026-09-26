package pl.neene.haft.auth.records;

import java.time.Duration;

public record AuthResult(String refreshToken, String accessToken, Duration expiresIn) {
}
