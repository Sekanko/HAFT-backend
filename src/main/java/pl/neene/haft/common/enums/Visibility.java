package pl.neene.haft.common.enums;

import lombok.Getter;

@Getter
public enum Visibility {
    PRIVATE(false),
    PUBLIC(true);

    private final boolean isPublic;

    Visibility(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
