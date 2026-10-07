package pl.neene.haft.embroideryStorage.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class CrossStitchPatternNotFoundException extends UnauthorizedException {
    public CrossStitchPatternNotFoundException(String message) {
        super(message);
    }
}
