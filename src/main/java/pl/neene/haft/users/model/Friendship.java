package pl.neene.haft.users.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.neene.haft.auth.model.NeeNeUser;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private NeeNeUser receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    private NeeNeUser sender;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @CreationTimestamp
    @Column(name = "creation_at", nullable = false, updatable = false)
    private Instant creationAt;

    @Column(name = "response_at")
    private Instant responseAt;

    public void setResponseAt(Instant responseAt) {
        if(this.responseAt != null) {
            throw new  IllegalStateException("Response time is already set");
        }
        this.responseAt = responseAt;
    }
}
