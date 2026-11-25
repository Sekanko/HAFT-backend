package pl.neene.haft.chat.records;

import pl.neene.haft.chat.enums.ChatRoomType;

import java.util.UUID;

public record DirectChatResponse(
        UUID chatRoomId,
        ChatRoomType type
) {
}
