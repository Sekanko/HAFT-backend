package pl.neene.haft.users.records;

import java.util.List;

public record UsersSliceResponse(
        List<BasicUserInfo> users,
        boolean hasNext
) {
}
