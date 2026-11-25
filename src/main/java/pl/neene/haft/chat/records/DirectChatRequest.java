package pl.neene.haft.chat.records;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DirectChatRequest(
        @NotNull
        UUID otherUserID
) {
}
