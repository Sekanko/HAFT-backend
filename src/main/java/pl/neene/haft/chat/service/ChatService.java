package pl.neene.haft.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;
import pl.neene.haft.chat.enums.ChatParticipantRole;
import pl.neene.haft.chat.enums.ChatRoomType;
import pl.neene.haft.chat.model.ChatMessage;
import pl.neene.haft.chat.model.ChatParticipant;
import pl.neene.haft.chat.model.ChatParticipantId;
import pl.neene.haft.chat.model.ChatRoom;
import pl.neene.haft.chat.records.ChatMessageResponse;
import pl.neene.haft.chat.records.DirectChatResponse;
import pl.neene.haft.chat.repository.ChatMessageRepository;
import pl.neene.haft.chat.repository.ChatParticipantRepository;
import pl.neene.haft.chat.repository.ChatRoomRepository;
import pl.neene.haft.common.exceptions.UnauthorizedException;
import pl.neene.haft.common.services.S3Service;
import pl.neene.haft.users.service.FriendshipService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final FriendshipService friendshipService;
    private final ChatRoomRepository chatRoomRepository;
    private final NeeNeUserRepository userRepository;
    private final ChatParticipantRepository participantRepository;
    private final S3Service s3Service;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public DirectChatResponse createDirect(UUID currentUserId, UUID otherUserId){
        if (currentUserId.equals(otherUserId)) throw new IllegalArgumentException();
        if (!friendshipService.areUsersFriends(currentUserId, otherUserId))
            throw new UnauthorizedException("Users can chat only with their friends!");

        String dmKey = buildDmKey(currentUserId, otherUserId);

        Optional<ChatRoom> existing = chatRoomRepository.findByDmKey(dmKey);
        if (existing.isPresent()) {
            return directChatResponseFromChatRoom(existing.get());
        }

        ChatRoom chat = chatRoomRepository.save(new ChatRoom()
                .setType(ChatRoomType.DM)
                .setDmKey(dmKey));

        Instant creationTime = Instant.now();

        participantRepository.saveAll(List.of(
                createMemberChatParticipant(currentUserId, chat, creationTime),
                createMemberChatParticipant(otherUserId, chat, creationTime)
        ));

        return directChatResponseFromChatRoom(chat);
    }

    @Transactional
    public ChatMessageResponse sendMessage(UUID userId, UUID chatId, String message, byte[] img){
        //todo custom exceptions
        if (message == null && img == null) throw new IllegalArgumentException();
        ChatParticipant participant = participantRepository.findByUser_IdAndChatRoom_Id(userId, chatId).orElseThrow();

        if (message != null) message = message.trim();
        String imgKey = img == null ? null : "chat/" + chatId + "/" + UUID.randomUUID() + ".png";

        ChatRoom chatRoom = chatRoomRepository.getReferenceById(chatId);
        ChatMessage chatMessage = chatMessageRepository.saveAndFlush(
                new ChatMessage()
                    .setChatRoom(chatRoom)
                    .setContent(message)
                    .setSender(participant.getUser())
                    .setImageKey(imgKey)
        );

        chatRoomRepository.updateLastMessageAt(chatId, chatMessage.getCreatedAt());

        if (imgKey != null){
            s3Service.store(img, imgKey, false);
        }

        return new ChatMessageResponse(
                chatMessage.getUuid(),
                chatId,
                userId,
                message,
                s3Service.generateUrl(imgKey, false),
                chatMessage.getCreatedAt()
        );
    }


    private DirectChatResponse directChatResponseFromChatRoom(ChatRoom chat){
        return new DirectChatResponse(chat.getId(), chat.getType());
    }

    private ChatParticipant createMemberChatParticipant(UUID userId, ChatRoom chat, Instant time){
        return new ChatParticipant()
                .setId(new ChatParticipantId(chat.getId(), userId))
                .setChatRoom(chat)
                .setUser(userRepository.getReferenceById(userId))
                .setJoinedAt(time)
                .setLastReadAt(time)
                .setRole(ChatParticipantRole.MEMBER);
    }

    private String buildDmKey(UUID a, UUID b){
        String x = a.toString();
        String y = b.toString();
        return x.compareTo(y) < 0 ? x + ":" + y : y + ":" + x;
    }
}
