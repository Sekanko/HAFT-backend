package pl.neene.haft.realtime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPushService {
    private final SimpMessagingTemplate template;

    public void send(UUID userId, String channel, Object payload){
        template.convertAndSend("/topic/user." + userId + "." + channel, payload);
    }
}
