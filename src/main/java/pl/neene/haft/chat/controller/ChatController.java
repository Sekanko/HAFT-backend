package pl.neene.haft.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.neene.haft.chat.records.ChatMessageResponse;
import pl.neene.haft.chat.records.DirectChatRequest;
import pl.neene.haft.chat.records.DirectChatResponse;
import pl.neene.haft.chat.records.MessageRequest;
import pl.neene.haft.chat.service.ChatService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
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

}
