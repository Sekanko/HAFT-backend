package pl.neene.haft.chat.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.chat.enums.ChatParticipantRole;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "chat_participant")
public class ChatParticipant {
    @EmbeddedId
    private ChatParticipantId id;

    @MapsId("chatRoomId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "neene_user_id", nullable = false)
    private NeeNeUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatParticipantRole role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    private Instant lastReadAt;
}
