package pl.neene.haft.chat.records;

import pl.neene.haft.chat.enums.ChatRoomType;

import java.time.Instant;
import java.util.UUID;

public record ChatRoomSummaryResponse(
        UUID roomId,
        ChatRoomType type,
        String name,
        Instant lastMessageAt,
        Instant lastReadAt
) {
}
