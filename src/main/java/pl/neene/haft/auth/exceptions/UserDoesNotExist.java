package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class UserDoesNotExist extends UnauthorizedException {
    public UserDoesNotExist(String message) {
        super(message);
    }
}
