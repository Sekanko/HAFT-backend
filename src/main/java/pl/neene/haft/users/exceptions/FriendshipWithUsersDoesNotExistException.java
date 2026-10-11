package pl.neene.haft.users.exceptions;

import pl.neene.haft.common.exceptions.UnauthorizedException;

public class FriendshipWithUsersDoesNotExistException extends UnauthorizedException {
    public FriendshipWithUsersDoesNotExistException(String message) {
        super(message);
    }
}
