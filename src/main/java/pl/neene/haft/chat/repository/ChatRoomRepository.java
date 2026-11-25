package pl.neene.haft.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.chat.model.ChatRoom;

import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByDmKey(String dmKey);
}
