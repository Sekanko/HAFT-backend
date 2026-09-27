package pl.neene.haft.common.exceptions;

public class UnauthorizedException extends ApiException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
