package pl.neene.haft.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.neene.haft.chat.model.ChatRoom;

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
}
