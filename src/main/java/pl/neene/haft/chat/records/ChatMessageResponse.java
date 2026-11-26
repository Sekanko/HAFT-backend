package pl.neene.haft.chat.records;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID chatRoomId,
        UUID senderId,
        String content,
        String imageUrl,
        Instant createdAt
) {}
