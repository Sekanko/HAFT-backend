package pl.neene.haft.auth.exceptions;

public class RefreshTokenDoesNotExistException extends RuntimeException {
    public RefreshTokenDoesNotExistException(String message) {
        super(message);
    }
}
