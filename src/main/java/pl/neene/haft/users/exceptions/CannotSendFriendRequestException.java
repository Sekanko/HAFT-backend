package pl.neene.haft.users.exceptions;

import pl.neene.haft.common.exceptions.ConflictException;

public class CannotSendFriendRequestException extends ConflictException {
    public CannotSendFriendRequestException(String message) {
        super(message);
    }
}
