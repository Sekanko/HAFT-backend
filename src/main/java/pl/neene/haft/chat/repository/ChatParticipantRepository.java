package pl.neene.haft.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.neene.haft.chat.model.ChatParticipant;
import pl.neene.haft.chat.model.ChatParticipantId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
    Optional<ChatParticipant> findByUser_IdAndChatRoom_Id(UUID userId, UUID chatRoomId);

    boolean existsByUser_IdAndChatRoom_Id(UUID userId, UUID chatRoomId);

    @Modifying
    @Query("""
        UPDATE ChatParticipant p
        SET p.lastReadAt = :time
        WHERE p.user.id = :userId AND p.chatRoom.id = :roomId
        AND (p.lastReadAt IS NULL OR p.lastReadAt < :time)
        """)
    void updateLastReadAt(@Param("userId") UUID userId, @Param("roomId") UUID roomId, @Param("time") Instant time);
}
