package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.ConflictException;

public class UserAlreadyExistsException extends ConflictException {
    public UserAlreadyExistsException(){
        super("User with this email already exists");
    }
}
