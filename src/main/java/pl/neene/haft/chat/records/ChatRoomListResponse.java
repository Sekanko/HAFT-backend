package pl.neene.haft.chat.records;

import java.util.List;

public record ChatRoomListResponse(
        List<ChatRoomSummaryResponse> rooms,
        boolean hasNext
) {}
