package pl.neene.haft.embroidery.records;

import java.util.UUID;

public record CreatePatternResponse(String message, UUID patternId) {
}
