package pl.neene.haft.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.neene.haft.chat.records.*;
import pl.neene.haft.chat.service.ChatService;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    private ChatService chatService;

    @PostMapping("/direct")
    public ResponseEntity<DirectChatResponse> createDirectChat(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DirectChatRequest request
    ){
        DirectChatResponse response = chatService.createDirect(userId, request.otherUserID());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{chatId}/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID chatId,
            @Valid @RequestPart(value = "metadata", required = false) MessageRequest metadata,
            @RequestPart(value = "img", required = false) MultipartFile img
    ){
        try {
            String message = metadata == null ? null : metadata.message();
            byte[] bytes = (img == null || img.isEmpty()) ? null : img.getBytes();

            ChatMessageResponse response = chatService.sendMessage(userId, chatId, message, bytes);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ChatHistoryResponse> getChatHistory(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID chatId,
            @RequestParam (required = false) Instant beforeTime,
            @RequestParam (required = false) UUID beforeId,
            @RequestParam (defaultValue = "30") int size
    ){
        ChatHistoryResponse response = chatService.getHistory(userId, chatId, beforeTime, beforeId, size);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{chatId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID chatId
    ){
        chatService.markAsRead(userId, chatId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ChatRoomListResponse> getMyChats(
            @AuthenticationPrincipal UUID userId,
            @PageableDefault(size = 20) Pageable pageable
    ){
        ChatRoomListResponse response = chatService.getMyChats(userId, pageable);
        return ResponseEntity.ok().body(response);
    }

}
