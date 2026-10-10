package pl.neene.haft.embroideryStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.neene.haft.embroideryStorage.model.CrossStitchPattern;

import java.util.Optional;
import java.util.UUID;

public interface CrossStitchPatternRepository extends JpaRepository<CrossStitchPattern, UUID> {
    Optional<CrossStitchPattern> findByIdAndCreatorId(UUID id, UUID creatorId);
}
