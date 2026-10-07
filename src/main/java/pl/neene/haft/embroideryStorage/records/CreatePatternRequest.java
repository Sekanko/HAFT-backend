package pl.neene.haft.embroideryStorage.records;

public record CreatePatternRequest(
        String title,
        boolean isPublic,
        int canvasDensity,
        int canvasWidth,
        int canvasHeight,
        int colorCount
) {
}
