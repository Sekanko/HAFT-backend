package pl.neene.haft.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;
import pl.neene.haft.common.exceptions.ConflictException;
import pl.neene.haft.users.model.Friendship;
import pl.neene.haft.users.model.FriendshipStatus;
import pl.neene.haft.users.records.AddFriendRequest;
import pl.neene.haft.users.records.AddFriendResponse;
import pl.neene.haft.users.records.FriendAcceptedResponse;
import pl.neene.haft.users.repositories.FriendshipRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final NeeNeUserRepository neeNeUserRepository;

    public AddFriendResponse createFriendRequest(AddFriendRequest addFriendRequest, UUID senderId) {
        UUID receiverId = addFriendRequest.receiverId();

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
            return new RuntimeException("Someone tried to accept friendship request ");
        });

        if (friendshipRequest.getStatus() != FriendshipStatus.PENDING) {
            throw new ConflictException("Friendship request already accepted");
        }

        friendshipRequest.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendshipRequest);

        return new FriendAcceptedResponse(FriendshipStatus.ACCEPTED, friendshipId);
    }

    public void deleteFriendship(UUID userId, UUID friendshipId) {
        Friendship friendship = friendshipRepository.findByIdAndUser(friendshipId, userId).orElseThrow( () -> {
            log.warn("Someone tried to delete friendship request {} with receiver {}", friendshipId, userId);
            return new RuntimeException("Someone tried to delete friendship request");
        });

        friendshipRepository.delete(friendship);
    }
}
