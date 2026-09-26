package pl.neene.haft.auth.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.neene.haft.auth.records.*;
import pl.neene.haft.auth.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final static String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request){
        RegisterUserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> loginUserWithCredentials(@Valid @RequestBody LoginUserRequest request){
        AuthResult result = authService.loginUser(request);
        ResponseCookie cookie = createRefreshTokenCookieFromAuthResult(result);
        LoginUserResponse response = new LoginUserResponse(result.accessToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshTokens(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken){
        AuthResult result = authService.refreshToken(refreshToken);
        ResponseCookie cookie = createRefreshTokenCookieFromAuthResult(result);
        RefreshResponse response = new RefreshResponse(result.accessToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    private ResponseCookie createRefreshTokenCookieFromAuthResult(AuthResult result){
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(result.expiresIn())
                .value(result.refreshToken())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME) String refreshToken){
        authService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
