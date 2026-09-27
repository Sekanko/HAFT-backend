package pl.neene.haft.auth.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.exceptions.UserAlreadyExistsException;
import pl.neene.haft.auth.exceptions.UserDoesNotExist;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.records.*;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final NeeNeUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public RegisterUserResponse register(@Valid RegisterUserRequest request) {
        String email = request.email();
        
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration attempt using email: {}, but user already exists!", email);
            throw new UserAlreadyExistsException();
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

    public AuthResult loginUser(LoginUserRequest request) {
        String email = request.email();
        String password = request.password();

        NeeNeUser user = userRepository.findByEmail(email).orElseThrow(()-> {
            log.warn("Login attempt using email: {}, but user does not exist!", email);
            return new UserDoesNotExist(email);
        });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Login attempt using email: {}, but password is incorrect!", email);
            throw new UserDoesNotExist(email);
        }

        RefreshTokenResult refreshTokenResult = refreshTokenService.generateRefreshToken(user);
        String accessToken = jwtService.generateJWT(user);

        return new AuthResult(refreshTokenResult.refreshToken(), accessToken, refreshTokenResult.expiresIn());
    }

    public AuthResult refreshToken(String rawToken){
        RefreshTokenResult rotationResult = refreshTokenService.refresh(rawToken);
        String accessToken = jwtService.generateJWT(rotationResult.user());
        return new AuthResult(rotationResult.refreshToken(), accessToken, rotationResult.expiresIn());
    }


    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }
}
