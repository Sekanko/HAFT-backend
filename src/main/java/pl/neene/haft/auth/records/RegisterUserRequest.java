package pl.neene.haft.auth.records;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email is not valid")
        @Size(max = 255, message = "Email cannot be longer than 255 characters")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 5, max = 72, message = "Password must be between 5 and 72 characters long")
        String password,

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 255, message = "Name cannot be longer than 255 characters")
        String firstName,

        @NotBlank(message = "Surname cannot be blank")
        @Size(max = 255, message = "Surname cannot be longer than 255 characters")
        String  lastName
) {
}
