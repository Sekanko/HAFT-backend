package pl.neene.haft.embroideryStorage.records;

import java.util.UUID;

public record CreatePatternResponse(String message, UUID patternId) {
}
