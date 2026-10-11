package pl.neene.haft.users.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.neene.haft.users.records.AddFriendRequest;
import pl.neene.haft.users.records.AddFriendResponse;
import pl.neene.haft.users.records.FriendshipBodyRequest;
import pl.neene.haft.users.service.FriendshipService;

import java.util.UUID;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/request")
    public ResponseEntity<AddFriendResponse> addFriendRequest(@RequestBody AddFriendRequest addFriendRequest, @AuthenticationPrincipal UUID senderId) {
        AddFriendResponse result = friendshipService.createFriendRequest(addFriendRequest, senderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@AuthenticationPrincipal UUID userId, @RequestBody FriendshipBodyRequest friendshipRequest) {
        var result = friendshipService.acceptFriendRequest(userId, friendshipRequest.friendshipId());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFriendRequest(@AuthenticationPrincipal UUID userId, @RequestBody FriendshipBodyRequest friendshipRequest) {
        friendshipService.deleteFriendship(userId, friendshipRequest.friendshipId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
