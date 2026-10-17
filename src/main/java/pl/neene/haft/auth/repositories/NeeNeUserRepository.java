package pl.neene.haft.auth.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.users.model.NeeNeUserSearchView;

import java.util.Optional;
import java.util.UUID;

public interface NeeNeUserRepository extends JpaRepository<NeeNeUser, UUID> {
    boolean existsByEmail(String email);
    Optional<NeeNeUser> findByEmail(String email);

    @Query(value = """
        SELECT id, first_name AS firstName, last_name AS lastName, avatar_key AS avatarKey
        FROM neene_user
        WHERE lower(first_name) LIKE (lower(:q) || '%')
        OR lower(last_name)     LIKE (lower(:q) || '%')
        OR lower(:q) <% (lower(first_name) || ' ' || lower(last_name))
        ORDER BY 
                    (lower(last_name) LIKE lower(:q) OR lower(first_name) LIKE lower(:q)) DESC,
                    word_similarity(lower(:q), lower(first_name || ' ' || last_name)) DESC
        """, nativeQuery = true)
    Slice<NeeNeUserSearchView> search(@Param("q")String q, Pageable pageable);
}
