package pl.neene.haft.realtime.interceptor;

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
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionInterceptor implements ChannelInterceptor {
    private static final Pattern USER_TOPIC = Pattern.compile("^/topic/user\\.([^.]+)\\..+$");

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.SUBSCRIBE) return message;

        Principal user = accessor.getUser();
        String destination = accessor.getDestination();
        Matcher matcher = destination == null ? null : USER_TOPIC.matcher(destination);

        if (user == null || matcher == null || !matcher.matches() || !matcher.group(1).equals(user.getName())) {
            log.warn("Subscription denied: user={}, destination={}", user == null ? null : user.getName(), destination);
            throw new MessageDeliveryException("Forbidden");
        }

        return message;
    }
}
