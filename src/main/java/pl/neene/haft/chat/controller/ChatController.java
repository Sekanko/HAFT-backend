package pl.neene.haft.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.neene.haft.chat.records.DirectChatRequest;
import pl.neene.haft.chat.records.DirectChatResponse;
import pl.neene.haft.chat.service.ChatService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private ChatService chatService;

    @PostMapping("/direct")
    public ResponseEntity<DirectChatResponse> createDirectChat(
            @AuthenticationPrincipal UUID userID,
            @Valid @RequestBody DirectChatRequest request
    ){
        DirectChatResponse response = chatService.createDirect(userID, request.otherUserID());
        return ResponseEntity.ok().body(response);
    }
}
