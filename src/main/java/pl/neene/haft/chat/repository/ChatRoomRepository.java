package pl.neene.haft.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.neene.haft.chat.model.ChatRoom;
import pl.neene.haft.chat.model.MyRoomView;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByDmKey(String dmKey);

    @Modifying
    @Query("""
        UPDATE ChatRoom room
        SET room.lastMessageAt = :time
        WHERE room.id = :roomId
        AND (room.lastMessageAt IS NULL OR room.lastMessageAt < :time)
    """)
    void updateLastMessageAt(@Param("roomId") UUID roomId, @Param("time") Instant time);

    @Query("""
        SELECT r.id AS roomId, r.type AS type, r.name AS name, r.lastMessageAt AS lastMessageAt, p.lastReadAt AS lastReadAt
        FROM ChatParticipant p JOIN p.chatRoom r
        WHERE p.user.id = :userId
        ORDER BY r.lastMessageAt DESC NULLS LAST, r.id DESC
    """)
    Slice<MyRoomView> findMyRooms(@Param("userId") UUID userId, Pageable pageable);
}
