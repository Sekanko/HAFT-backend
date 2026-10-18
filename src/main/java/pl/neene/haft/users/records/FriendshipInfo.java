package pl.neene.haft.users.records;

import java.util.UUID;

public record FriendshipInfo(
        UUID friendshipId,
        BasicUserInfo friend
) {
}
