package pl.neene.haft.embroidery.records;

public record CreatePatternRequest(
        String title,
        boolean isPublic,
        int canvasDensity,
        int canvasWidth,
        int canvasHeight,
        int colorCount
) {
}
