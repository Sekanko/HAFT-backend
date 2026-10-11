package pl.neene.haft.embroideryStorage.records;

import java.util.UUID;

public record BasicPatternInfo(
        UUID id,
        String title,
        String ownerName,
        String url
) {
}