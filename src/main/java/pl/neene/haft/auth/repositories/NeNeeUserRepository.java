package pl.neene.haft.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.auth.model.NeeNeUser;

import java.util.UUID;

public interface NeNeeUserRepository extends JpaRepository<NeeNeUser, UUID> {
    boolean existsByEmail(String email);
}
