package pl.neene.haft.auth.records;

import pl.neene.haft.auth.model.NeeNeUser;

public record TokenRotationResult(
        NeeNeUser user,
        String accessToken
) {
}
