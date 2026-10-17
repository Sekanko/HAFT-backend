package pl.neene.haft.auth.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.neene.haft.auth.records.LoginUserRequest;
import pl.neene.haft.auth.services.AuthService;
import pl.neene.haft.common.config.AppProfile;

import java.util.Map;

@RestController
@Profile(AppProfile.LOCAL)
@Hidden
@RequiredArgsConstructor
public class SwaggerAuthController {
    private final AuthService authService;

    @PostMapping("/swagger-auth")
    public Map<String, String> getSwaggerAuth(@RequestParam("username") String username, @RequestParam("password") String password) {
        var result = authService.loginUser(new LoginUserRequest(username, password));
        return Map.of(
                "access_token", result.accessToken(),
                "token_type", "Bearer"
        );
    }
}
