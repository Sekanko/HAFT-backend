package pl.neene.haft.users.exceptions;

import pl.neene.haft.common.exceptions.ConflictException;

public class FriendshipAlreadyAcceptedException extends ConflictException {
    public FriendshipAlreadyAcceptedException(String message) {
        super(message);
    }
}
