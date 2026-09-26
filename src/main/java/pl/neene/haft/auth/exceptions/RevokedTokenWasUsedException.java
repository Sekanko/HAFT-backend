package pl.neene.haft.auth.exceptions;

public class RevokedTokenWasUsedException extends RuntimeException {
    public RevokedTokenWasUsedException(String message) {
        super(message);
    }
}
