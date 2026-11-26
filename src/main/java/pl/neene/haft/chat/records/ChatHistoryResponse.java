package pl.neene.haft.chat.records;

import java.util.List;

public record ChatHistoryResponse(
        List<ChatMessageResponse> messages,
        boolean hasNext
) {

}
