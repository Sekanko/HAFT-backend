package pl.neene.haft.embroidery.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.neene.haft.embroidery.model.CrossStitchPattern;

import java.util.Optional;
import java.util.UUID;

public interface CrossStitchPatternRepository extends JpaRepository<CrossStitchPattern, UUID> {
    Optional<CrossStitchPattern> findByIdAndCreatorId(UUID id, UUID creatorId);

    @Query("""
        SELECT csp 
        FROM CrossStitchPattern csp 
        JOIN FETCH csp.creator 
        WHERE csp.isPublic = true 
    """)
    Page<CrossStitchPattern> findAllPublicPatterns(Pageable pageable);
}
