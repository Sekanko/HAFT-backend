package pl.neene.haft.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.exceptions.UserDoesNotExist;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;
import pl.neene.haft.users.exceptions.*;
import pl.neene.haft.users.model.Friendship;
import pl.neene.haft.users.model.FriendshipStatus;
import pl.neene.haft.users.records.*;
import pl.neene.haft.users.repositories.FriendshipRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final NeeNeUserRepository neeNeUserRepository;
    private final NeeNeUserService userService;

    public AddFriendResponse createFriendRequest(AddFriendRequest addFriendRequest, UUID senderId) {
        UUID receiverId = addFriendRequest.receiverId();

        if (!neeNeUserRepository.existsById(receiverId)){
            log.warn("Someone tried to send friend request to non-existing user {}", receiverId);
            throw new UserDoesNotExist("User with provided id doesn't exist");
        }

        if (receiverId.equals(senderId)) throw new CannotSendFriendRequestException("User cannot send friend request to himself");

        if (friendshipRepository.existsByUsersIds(senderId, receiverId)) throw new FriendshipAlreadyExistsException("Friendship already exists");

        NeeNeUser sender =  neeNeUserRepository.getReferenceById(senderId);
        NeeNeUser receiver = neeNeUserRepository.getReferenceById(receiverId);

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);

        return AddFriendResponse.CREATED;
    }

    public FriendAcceptedResponse acceptFriendRequest(UUID receiverId, UUID friendshipId) {
        Friendship friendshipRequest = friendshipRepository.findByIdAndReceiverId(friendshipId, receiverId).orElseThrow(() -> {
            log.warn("Someone tried to accept friendship request {} with receiver {}", friendshipId, receiverId);
            return new FriendshipWithUsersDoesNotExistException("Someone unauthorized tried to accept friendship request ");
        });

        if (friendshipRequest.getStatus() != FriendshipStatus.PENDING) {
            throw new FriendshipAlreadyAcceptedException("Friendship request already accepted");
        }

        friendshipRequest.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRequest.setResponseAt(Instant.now());
        friendshipRepository.save(friendshipRequest);

        return new FriendAcceptedResponse(FriendshipStatus.ACCEPTED, friendshipId);
    }

    public void deleteFriendship(UUID userId, UUID friendshipId) {
        Friendship friendship = friendshipRepository.findByIdAndUser(friendshipId, userId).orElseThrow( () -> {
            log.warn("Someone tried to delete friendship that doesn't exist with request {} and receiver {}", friendshipId, userId);
            return new FriendshipNotFoundException("Friendship with provided data doesn't exist");
        });

        friendshipRepository.delete(friendship);
    }

    public InvitesAndFriendsResponse getInviteAndFriendsList(UUID userId) {
        List<Friendship> friendships = friendshipRepository.findInvitesAndFriends(userId);
        Map<FriendshipStatus, List<FriendshipInfo>> usersByFriendshipStatus = friendships.stream()
                .collect(Collectors.groupingBy(
                        Friendship::getStatus,
                        Collectors.mapping(friendship ->  getFriendshipInfo(friendship, userId), Collectors.toList()))
                );

        return new InvitesAndFriendsResponse(
                usersByFriendshipStatus.get(FriendshipStatus.PENDING),
                usersByFriendshipStatus.get(FriendshipStatus.ACCEPTED)
        );
    }

    private FriendshipInfo getFriendshipInfo(Friendship friendship, UUID userId){
        return new FriendshipInfo(
                friendship.getId(),
                mapEntityUserToBasicUserInfo(getOtherUser(friendship, userId))
        );
    }

    private NeeNeUser getOtherUser(Friendship friendship, UUID userId){
        UUID receiverId = friendship.getReceiver().getId();
        if (receiverId.equals(userId)) return friendship.getSender();

        return friendship.getReceiver();
    }

    private BasicUserInfo mapEntityUserToBasicUserInfo(NeeNeUser user){
        return new BasicUserInfo(user.getId(), user.getFirstName(), user.getLastName(), user.getAvatarKey());
    }
}
