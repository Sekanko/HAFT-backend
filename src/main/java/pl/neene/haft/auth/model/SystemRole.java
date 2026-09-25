package pl.neene.haft.auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

public enum SystemRole {
    ADMIN, USER, MODERATOR;
}