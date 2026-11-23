package pl.neene.haft.realtime.interceptor;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.neene.haft.auth.services.JwtAuthenticationFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtAuthenticationFactory jwtAuthFactory;

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) return message;

        String header = accessor.getFirstNativeHeader("Authorization");
        try {
            Authentication auth = jwtAuthFactory
                    .fromHeader(header)
                    .orElseThrow(() -> new MessageDeliveryException("Unauthorized"));

            accessor.setUser(auth);
        } catch (JwtException e) {
            log.info("JWT token is invalid:{}", e.getMessage());
        }
        return message;
    }
}
