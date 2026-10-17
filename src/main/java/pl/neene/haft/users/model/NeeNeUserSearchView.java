package pl.neene.haft.users.model;

import java.util.UUID;

public interface NeeNeUserSearchView {
    UUID getId();
    String getFirstName();
    String getLastName();
    String getAvatarKey();
}
