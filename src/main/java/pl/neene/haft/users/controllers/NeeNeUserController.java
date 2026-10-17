package pl.neene.haft.users.controllers;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.neene.haft.users.records.BasicUserInfo;
import pl.neene.haft.users.records.UsersSliceResponse;
import pl.neene.haft.users.service.NeeNeUserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class NeeNeUserController {
    private final NeeNeUserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<BasicUserInfo> getBasicUserInfo(@PathVariable UUID userId) {
        BasicUserInfo userInfo = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userInfo);
    }

    @GetMapping("/search")
    public ResponseEntity<UsersSliceResponse> search(
            @RequestParam("query") String query,
            @ParameterObject Pageable pageable
    ) {
        UsersSliceResponse result = userService.searchUsers(query, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
