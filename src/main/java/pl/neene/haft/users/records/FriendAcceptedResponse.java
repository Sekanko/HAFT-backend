package pl.neene.haft.users.records;

import pl.neene.haft.users.model.FriendshipStatus;

import java.util.UUID;

public record FriendAcceptedResponse(
        FriendshipStatus status,
        UUID friendshipId
) {
}
