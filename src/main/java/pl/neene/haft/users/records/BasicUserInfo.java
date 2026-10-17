package pl.neene.haft.users.records;

import java.util.UUID;

public record BasicUserInfo(
        UUID id,
        String firstName,
        String lastName,
        String avatarUrl
) {
}
