package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.BadRequestException;

public class RefreshTokenDoesNotExistException extends RuntimeException {
    public RefreshTokenDoesNotExistException(String message) {
        super(message);
    }
}
