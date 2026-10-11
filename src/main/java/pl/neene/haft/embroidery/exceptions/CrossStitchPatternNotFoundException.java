package pl.neene.haft.embroidery.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class CrossStitchPatternNotFoundException extends UnauthorizedException {
    public CrossStitchPatternNotFoundException(String message) {
        super(message);
    }
}
