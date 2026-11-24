package pl.neene.haft.chat.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record ChatParticipantId(UUID chatRoomId, UUID userId) implements Serializable {}