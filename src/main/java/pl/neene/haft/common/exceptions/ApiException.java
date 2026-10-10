package pl.neene.haft.common.exceptions;

public abstract class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
