package pl.neene.haft.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.users.model.Friendship;

import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {
}
