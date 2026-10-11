package pl.neene.haft.embroidery.records;

import pl.neene.haft.common.enums.Visibility;

import java.util.UUID;

public record CrossStitchPatternResponse(
        UUID id,
        String title,
        Visibility visibility,
        int canvasHeight,
        int canvasWidth,
        int canvasDensity,
        ObjectS3Url objectS3Url
) {
}
