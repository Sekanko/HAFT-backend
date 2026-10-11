package pl.neene.haft.embroidery.records;

public record DeletePatternResponse(String message) {
    public static final DeletePatternResponse SUCCESS = new DeletePatternResponse("Pattern deleted successfully");
}
