package pl.neene.haft.auth.exceptions;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist(String message) {
        super(message);
    }
}
