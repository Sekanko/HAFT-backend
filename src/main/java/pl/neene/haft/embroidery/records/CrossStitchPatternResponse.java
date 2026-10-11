package pl.neene.haft.embroidery.records;

import java.util.UUID;

public record CrossStitchPatternResponse(
        UUID id,
        String title,
        boolean isPublic,
        int canvasHeight,
        int canvasWidth,
        int canvasDensity,
        ObjectS3Url objectS3Url
) {
}
