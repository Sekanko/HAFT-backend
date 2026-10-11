package pl.neene.haft.users.exceptions;

import pl.neene.haft.common.exceptions.ConflictException;

public class FriendshipAlreadyExistsException extends ConflictException {
    public FriendshipAlreadyExistsException(String message) {
        super(message);
    }
}
