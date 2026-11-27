package pl.neene.haft.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.neene.haft.chat.records.ChatMessageSentEvent;
import pl.neene.haft.chat.repository.ChatParticipantRepository;
import pl.neene.haft.realtime.service.UserPushService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessagePushListener {
    private final ChatParticipantRepository participantRepository;
    private final UserPushService pushService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageSent(ChatMessageSentEvent event){
        for (UUID participantId : participantRepository.findUserIdsByChatRoomId(event.chatRoomId())){
            try {
                pushService.send(participantId, "messages", event.message());
            } catch (RuntimeException e){
                log.error("Cannot push message to the user with id: {}", participantId, e);
            }
        }
    }
}
