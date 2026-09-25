package pl.neene.haft.auth.exceptions;

import pl.neene.haft.common.exceptions.BadRequestException;

public class UserAlreadyExistsException extends BadRequestException {
    public UserAlreadyExistsException(String email){
        super("User with this email already exists:" + email);
    }
}
