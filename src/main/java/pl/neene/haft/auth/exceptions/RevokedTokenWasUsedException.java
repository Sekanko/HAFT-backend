package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class RevokedTokenWasUsedException extends UnauthorizedException {
    public RevokedTokenWasUsedException(String message) {
        super(message);
    }
}
