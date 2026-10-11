package pl.neene.haft.embroidery.records;

import java.util.UUID;

public record BasicPatternInfo(
        UUID id,
        String title,
        String ownerName,
        String url
) {
}