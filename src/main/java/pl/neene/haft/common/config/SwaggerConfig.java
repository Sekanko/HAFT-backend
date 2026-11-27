package pl.neene.haft.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile(AppProfile.LOCAL)
public class SwaggerConfig {
    private static final String AUTH_SCHEME = "swagger-auth";

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerChain(HttpSecurity http) {
        return http
                .securityMatcher("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-auth")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("NeeNeApi").version("0.1").description("""
                ## How chat works
            
                - A user connects once over WebSocket and subscribes to their **own** channel:
                  `/topic/user.{userId}.messages`. Individual conversations are not subscribed to.
                - Conversations (rooms) and their participants are stored in the database.
                  Opening a conversation means ordinary REST requests with `{roomId}` in the path.
                - When someone sends a message (`POST /api/chats/{roomId}/messages`), the server pushes it
                  to the channel of every participant of the room. The `chatRoomId` field of the message
                  tells which conversation it belongs to.
                - After a disconnect, fetch missed messages with `GET /api/chats/{roomId}/messages`.
            
                ## WebSocket (STOMP)
            
                - Endpoint: `ws://host/ws`
                - `CONNECT` frame header: `Authorization: Bearer <access token>`
                - Subscription: `/topic/user.{ownUserId}.messages`
                - Payload: `ChatMessageResponse`
                """))
                .components(new Components().addSecuritySchemes(AUTH_SCHEME,
                        new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(new OAuthFlows().password(
                            new OAuthFlow().tokenUrl("/swagger-auth")))))
                .addSecurityItem(new SecurityRequirement().addList(AUTH_SCHEME));
    }
}
