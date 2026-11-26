package pl.neene.haft.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.neene.haft.chat.model.ChatMessage;

import java.time.Instant;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    @Query("""
    SELECT msg FROM ChatMessage msg
    WHERE msg.chatRoom.id = :roomId
    ORDER BY msg.createdAt DESC, msg.uuid DESC
    """)
    Slice<ChatMessage> findLatest(@Param("roomId") UUID roomId, Pageable pageable);

    @Query("""
    SELECT msg FROM ChatMessage msg
    WHERE msg.chatRoom.id = :roomId
      AND (msg.createdAt < :beforeTime
           OR (msg.createdAt = :beforeTime AND msg.uuid < :beforeId))
    ORDER BY msg.createdAt DESC, msg.uuid DESC
    """)
    Slice<ChatMessage> findBefore(@Param("roomId") UUID roomId,
                                  @Param("beforeTime") Instant beforeTime,
                                  @Param("beforeId") UUID beforeId,
                                  Pageable pageable);
}
