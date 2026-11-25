package pl.neene.haft.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.chat.model.ChatParticipant;
import pl.neene.haft.chat.model.ChatParticipantId;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
}
