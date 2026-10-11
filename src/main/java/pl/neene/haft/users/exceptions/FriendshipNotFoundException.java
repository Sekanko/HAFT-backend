package pl.neene.haft.users.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class FriendshipNotFoundException extends UnauthorizedException {
    public FriendshipNotFoundException(String message) {
        super(message);
    }
}
