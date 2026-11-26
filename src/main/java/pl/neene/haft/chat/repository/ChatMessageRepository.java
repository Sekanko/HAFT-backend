package pl.neene.haft.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.chat.model.ChatMessage;

import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
}
