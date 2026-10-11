package pl.neene.haft.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.neene.haft.users.model.Friendship;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findByIdAndReceiverId(UUID friendshipId, UUID receiverId);

    @Query("SELECT f FROM Friendship f WHERE f.id = :friendshipId and (f.receiver.id = :userId or f.sender.id = :userId)")
    Optional<Friendship> findByIdAndUser(UUID friendshipId, UUID userId);

    boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
}
