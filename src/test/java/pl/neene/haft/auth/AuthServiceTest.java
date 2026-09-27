package pl.neene.haft.auth;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.neene.haft.auth.exceptions.UserAlreadyExistsException;
import pl.neene.haft.auth.exceptions.UserDoesNotExist;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.records.*;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;
import pl.neene.haft.auth.services.AuthService;
import pl.neene.haft.auth.services.JwtService;
import pl.neene.haft.auth.services.RefreshTokenService;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private NeeNeUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Nested
    class RegisterUser {
        RegisterUserRequest requestUser;

        @BeforeEach
        void setUp() {
            String email = "fuubar@mail.com";
            String password = "P@ssw0rd";
            String firstName = "John";
            String lastName = "Doe";
            requestUser = new RegisterUserRequest(email, password, firstName, lastName);
        }

        @Test
        void validRequest_returnsRegisterUserResponse() {
            when(userRepository.existsByEmail(requestUser.email())).thenReturn(false);
            when(passwordEncoder.encode(requestUser.password())).thenReturn("hashedPassword");

            RegisterUserResponse response = authService.register(requestUser);
            RegisterUserResponse expectedResponse = RegisterUserResponse.SUCCESS;

            verify(userRepository, times(1)).save(any());
            assertEquals(expectedResponse, response);
        }

        @Test
        void emailIsAlreadyInUse_throwsUserAlreadyExistsException() {
            when(userRepository.existsByEmail(requestUser.email())).thenReturn(true);
            assertThrows(UserAlreadyExistsException.class, () -> authService.register(requestUser));
        }
    }

    @Nested
    class LoginUser {
        LoginUserRequest requestUser;
        NeeNeUser existingUser;

        @BeforeEach
        void setUp(){
            String email = "fuubar@mail.com";
            String password = "P@ssw0rd";
            requestUser = new LoginUserRequest(email, password);

            existingUser = new NeeNeUser();
            existingUser.setEmail(email);
            existingUser.setPasswordHash("hashedPassword");
        }

        @Test
        void validRequest_returnsAuthResult(){
            when(userRepository.findByEmail(requestUser.email())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(requestUser.password(), existingUser.getPasswordHash())).thenReturn(true);

            RefreshTokenResult refreshTokenResult = new RefreshTokenResult(existingUser, "TokenExample", Duration.ofDays(30));
            String accessToken = "accessTokenExample";

            when(refreshTokenService.generateRefreshToken(existingUser)).thenReturn(refreshTokenResult);
            when(jwtService.generateJWT(existingUser)).thenReturn(accessToken);

            AuthResult expectedResult = new AuthResult(refreshTokenResult.refreshToken(), accessToken, refreshTokenResult.expiresIn());
            AuthResult result = authService.loginUser(requestUser);

            assertEquals(expectedResult, result);
        }

        @Test
        void userWithEmailDoesNotExist_throwsUserDoesNotExists(){
            when(userRepository.findByEmail(requestUser.email())).thenReturn(Optional.empty());
            assertThrows(UserDoesNotExist.class, () -> authService.loginUser(requestUser));
        }

        @Test
        void passwordDoesNotMatch_throwsUserDoesNotExists(){
            when(userRepository.findByEmail(requestUser.email())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(requestUser.password(), existingUser.getPasswordHash())).thenReturn(false);
            assertThrows(UserDoesNotExist.class, () -> authService.loginUser(requestUser));
        }
    }

    @Nested
    class RefreshToken {
        @Test
        void validToken_returnsNewAccessToken(){
            String rawToken = "rawTokenExample";
            RefreshTokenResult refreshTokenResult = new RefreshTokenResult(new NeeNeUser(), rawToken, Duration.ofDays(30));
            when(refreshTokenService.refresh(rawToken)).thenReturn(refreshTokenResult);
            String expectedAccessToken = "accessTokenExample";
            when(jwtService.generateJWT(refreshTokenResult.user())).thenReturn(expectedAccessToken);

            AuthResult expectedResult = new AuthResult(refreshTokenResult.refreshToken(), expectedAccessToken, refreshTokenResult.expiresIn());
            AuthResult result = authService.refreshToken(rawToken);

            assertEquals(expectedResult, result);
        }
    }

    @Nested
    class Logout {
        @Test
        void validToken_revokesToken(){
            String rawToken = "rawTokenExample";
            authService.logout(rawToken);
            verify(refreshTokenService, times(1)).revokeToken(rawToken);
        }
    }

}
