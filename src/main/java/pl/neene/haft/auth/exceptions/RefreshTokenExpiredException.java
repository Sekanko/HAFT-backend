package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class RefreshTokenExpiredException extends UnauthorizedException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
