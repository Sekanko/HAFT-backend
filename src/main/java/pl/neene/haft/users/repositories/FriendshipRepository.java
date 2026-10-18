package pl.neene.haft.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.neene.haft.users.model.Friendship;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
    Optional<Friendship> findByIdAndReceiverId(UUID friendshipId, UUID receiverId);

    @Query("SELECT f FROM Friendship f WHERE f.id = :friendshipId and (f.receiver.id = :userId or f.sender.id = :userId)")
    Optional<Friendship> findByIdAndUser(UUID friendshipId, UUID userId);

    @Query("""
        SELECT COUNT(f) > 0
        FROM Friendship f
        WHERE (f.receiver.id = :rId and f.sender.id = :sId)OR (f.receiver.id = :sId and f.sender.id = :rId)
    """)
    boolean existsByUsersIds(@Param("sId") UUID senderId, @Param("rId") UUID receiverId);

    @Query("""
        SELECT f 
        FROM Friendship f
        JOIN FETCH f.sender
        JOIN FETCH f.receiver
        WHERE f.receiver.id = :userId 
        OR (f.sender.id = :userId AND f.status = FriendshipStatus.ACCEPTED)
    """)
    List<Friendship> findInvitesAndFriends(UUID userId);
}
