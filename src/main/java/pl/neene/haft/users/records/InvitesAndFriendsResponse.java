package pl.neene.haft.users.records;

import java.util.List;

public record InvitesAndFriendsResponse(
        List<FriendshipInfo> invites,
        List<FriendshipInfo> friends
) {
}
