package pl.neene.haft.auth.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.exceptions.UserAlreadyExistsException;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.records.RegisterUserRequest;
import pl.neene.haft.auth.records.RegisterUserResponse;
import pl.neene.haft.auth.repositories.NeNeeUserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final NeNeeUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserResponse register(@Valid RegisterUserRequest request) {
        String email = request.email();
        
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration attempt using email: {}, but user already exists!", email);
            throw new UserAlreadyExistsException(email);
        }

        NeeNeUser user = new NeeNeUser();
        user.setEmail(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        log.info("User registered: {}", user);

        return RegisterUserResponse.SUCCESS;
    }
}
