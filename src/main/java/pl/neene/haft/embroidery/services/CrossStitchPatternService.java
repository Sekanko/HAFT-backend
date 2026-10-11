package pl.neene.haft.embroidery.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.neene.haft.auth.model.NeeNeUser;
import pl.neene.haft.auth.repositories.NeeNeUserRepository;
import pl.neene.haft.common.services.S3Service;
import pl.neene.haft.embroidery.exceptions.CrossStitchPatternNotFoundException;
import pl.neene.haft.embroidery.model.CrossStitchPattern;
import pl.neene.haft.embroidery.records.*;
import pl.neene.haft.embroidery.repositories.CrossStitchPatternRepository;

import java.util.UUID;
import java.util.function.BiConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrossStitchPatternService {
    private final CrossStitchPatternRepository crossStitchPatternRepository;
    private final S3Service s3Service;
    private final NeeNeUserRepository userRepository;

    public CreatePatternResponse createPattern(CreatePatternRequest request, byte[] thumbnail, byte[] patternImg, UUID userId) {
        String key = UUID.randomUUID().toString();
        String thumbnailKey = "thumbnails/" + key + ".json";
        String patternKey = "patterns/" + key + ".png";

        boolean isPublic = request.isPublic();
        try {
            s3Service.store(thumbnail, thumbnailKey, isPublic);
            s3Service.store(patternImg, patternKey, isPublic);
        } catch (Exception e) {
            s3Service.deleteObject(thumbnailKey, !isPublic);
            log.error("Failed to store pattern", e);
            throw new RuntimeException("Failed to store pattern") {
            };
        }

        NeeNeUser owner = userRepository.getReferenceById(userId);

        CrossStitchPattern newPattern = new CrossStitchPattern()
                .setCreator(owner)
                .setTitle(request.title())
                .setPublic(isPublic)
                .setCanvasDensity(request.canvasDensity())
                .setCanvasWidth(request.canvasWidth())
                .setCanvasHeight(request.canvasHeight())
                .setColorCount(request.colorCount())
                .setThumbnailKey(thumbnailKey)
                .setPatternKey(patternKey);

        UUID id = crossStitchPatternRepository.save(newPattern).getId();
        log.info("Pattern created: {}", newPattern);
        return new CreatePatternResponse("Pattern saved successfully.", id);
    }

    public ChangePatternVisibilityResponse changePatternVisibility(UUID patternId, ChangePatternVisibilityRequest request, UUID userId){
        CrossStitchPattern pattern = getPatternOrThrow(patternId, userId);

        boolean isPublic = request.isPublic();

        if(pattern.isPublic() == isPublic){
            return new ChangePatternVisibilityResponse("Pattern is already " + (isPublic ? "public" : "private"));
        }
        callS3ServiceMethodTwice(s3Service::moveObject, pattern, s3Service::moveObject);
        pattern.setPublic(isPublic);

        crossStitchPatternRepository.save(pattern);

        log.info("Pattern visibility changed: {}", patternId);

        return new ChangePatternVisibilityResponse("Visibility changed to" + (isPublic ? " public" : " private"));
    }

    public DeletePatternResponse deletePattern(UUID patternId, UUID userId){
        CrossStitchPattern pattern = getPatternOrThrow(patternId, userId);

        callS3ServiceMethodTwice(s3Service::deleteObject, pattern);
        crossStitchPatternRepository.delete(pattern);

        return DeletePatternResponse.SUCCESS;
    }

    public CrossStitchPatternResponse getPattern(UUID patternId, UUID userId) {
        CrossStitchPattern pattern = getPatternOrThrow(patternId, userId);
        ObjectS3Url urls = getObjectUrl(pattern);
        log.info("Pattern retrieved: {}", patternId);
        return new CrossStitchPatternResponse(
                patternId,
                pattern.getTitle(),
                pattern.isPublic(),
                pattern.getCanvasHeight(),
                pattern.getCanvasWidth(),
                pattern.getCanvasDensity(),
                urls
        );
    }

    public Page<BasicPatternInfo> getPattersPage(Pageable pageable) {
        return crossStitchPatternRepository
                .findAllPublicPatterns(pageable)
                .map(entity -> {
                    NeeNeUser creator = entity.getCreator();
                    String fullName =  creator.getFirstName() + " " + creator.getLastName();

                    return new BasicPatternInfo(entity.getId(), entity.getTitle(), fullName, s3Service.generateUrl(entity.getPatternKey(), true));
                });
    }

    private void callS3ServiceMethodTwice(BiConsumer<String, Boolean> method, CrossStitchPattern pattern){
        callS3ServiceMethodTwice(method, pattern, s3Service::deleteObject);
    }

    private void callS3ServiceMethodTwice(BiConsumer<String, Boolean> method, CrossStitchPattern pattern, BiConsumer<String, Boolean> exceptionMethod){
        boolean isPublic = pattern.isPublic();
        String key1 = pattern.getThumbnailKey();
        String key2 = pattern.getPatternKey();

        try {
            method.accept(key1, isPublic);
            method.accept(key2, isPublic);
        } catch (Exception e) {
            exceptionMethod.accept(key1, !isPublic);
            log.error("Interaction with s3service went wrong!", e);
            throw new RuntimeException("S3 service is not available");
        }
    }

    private ObjectS3Url getObjectUrl(CrossStitchPattern pattern){
        boolean isPublic = pattern.isPublic();

        String thumbnailUrl = s3Service.generateUrl(pattern.getThumbnailKey(), isPublic);
        String patternUrl = s3Service.generateUrl(pattern.getPatternKey(), isPublic);

        return new ObjectS3Url(thumbnailUrl, patternUrl);
    }

    private CrossStitchPattern getPatternOrThrow(UUID patternId, UUID userId){
        return crossStitchPatternRepository.findByIdAndCreatorId(patternId, userId).orElseThrow(() -> {
            log.warn("Did not find pattern with id: {} for user: {}", patternId, userId);
            return new CrossStitchPatternNotFoundException("Cannot find pattern with provided id");
        });
    }

}
