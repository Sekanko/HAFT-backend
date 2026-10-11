package pl.neene.haft.embroidery.records;

import pl.neene.haft.common.enums.Visibility;

public record CreatePatternRequest(
        String title,
        Visibility visibility,
        int canvasDensity,
        int canvasWidth,
        int canvasHeight,
        int colorCount
) {
}
