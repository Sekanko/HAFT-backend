package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class RefreshTokenDoesNotExistException extends UnauthorizedException {
    public RefreshTokenDoesNotExistException(String message) {
        super(message);
    }
}
