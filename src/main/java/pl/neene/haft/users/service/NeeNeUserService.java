package pl.neene.haft.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.exceptions.UserDoesNotExist;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;
import pl.neene.haft.common.services.S3Service;
import pl.neene.haft.users.model.NeeNeUserSearchView;
import pl.neene.haft.users.records.BasicUserInfo;
import pl.neene.haft.users.records.UsersSliceResponse;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NeeNeUserService {
    private final NeeNeUserRepository userRepository;
    private final S3Service s3Service;

    public BasicUserInfo getUser(UUID userId) {
        NeeNeUser user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User not found with id {}", userId);
            return new UserDoesNotExist("User not found");
        });
        String avatarKey = user.getAvatarKey();
        String avatarUrl = avatarKey != null ? s3Service.generateUrl(avatarKey, true) : null;

        return new BasicUserInfo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                avatarUrl
        );
    }

    public UsersSliceResponse searchUsers(String query, Pageable pageable) {
        Slice<NeeNeUserSearchView> slice = userRepository.search(query, pageable);

        List<BasicUserInfo> usersInfo =  slice.getContent()
                .stream()
                .map(u -> new BasicUserInfo(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        getAvatarUrl(u.getAvatarKey())
                )).toList();

        return new UsersSliceResponse(usersInfo, slice.hasNext());
    }

    private String getAvatarUrl(String avatarKey) {
        return avatarKey != null ? s3Service.generateUrl(avatarKey, true) : null;
    }
}
