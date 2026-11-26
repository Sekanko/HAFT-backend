package pl.neene.haft.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.chat.model.ChatParticipant;
import pl.neene.haft.chat.model.ChatParticipantId;

import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
    Optional<ChatParticipant> findByUser_IdAndChatRoom_Id(UUID userId, UUID chatRoomId);

    boolean existsByUser_IdAndChatRoom_Id(UUID userId, UUID chatRoomId);
}
