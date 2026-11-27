package pl.neene.haft.chat.records;

import java.util.UUID;

public record ChatMessageSentEvent(
        UUID chatRoomId,
        ChatMessageResponse message
) {}