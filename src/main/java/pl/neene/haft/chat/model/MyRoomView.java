package pl.neene.haft.chat.model;

import pl.neene.haft.chat.enums.ChatRoomType;

import java.time.Instant;
import java.util.UUID;

public interface MyRoomView {
    UUID getRoomId();
    ChatRoomType getType();
    String getName();
    Instant getLastMessageAt();
    Instant getLastReadAt();
}
