package pl.neene.haft.embroidery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.neene.haft.embroidery.records.*;
import pl.neene.haft.embroidery.services.CrossStitchPatternService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/patterns")
@RequiredArgsConstructor
public class CrossStitchPatternController {
    private final CrossStitchPatternService crossStitchPatternService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreatePatternResponse> createPattern(
            @RequestPart("metadata") CreatePatternRequest metadata,
            @RequestPart("pattern") MultipartFile pattern,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @AuthenticationPrincipal UUID userId
    ){
        try {
            CreatePatternResponse result = crossStitchPatternService.createPattern(metadata, thumbnail.getBytes(), pattern.getBytes(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IOException e){
            log.error("Failed to collect bytes from MultipartFile", e);
            throw new IllegalStateException("Failed to create pattern");
        }
    }

    @PatchMapping("/patch/visibility/{patternId}")
    public ResponseEntity<ChangePatternVisibilityResponse> updatePattern(
            @AuthenticationPrincipal UUID userId,
            @RequestBody ChangePatternVisibilityRequest request,
            @PathVariable UUID patternId
            ){
        ChangePatternVisibilityResponse result = crossStitchPatternService.changePatternVisibility(patternId, request, userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/delete/{patternId}")
    public ResponseEntity<DeletePatternResponse> deletePattern(@PathVariable UUID patternId, @AuthenticationPrincipal UUID userId){
        DeletePatternResponse response = crossStitchPatternService.deletePattern(patternId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{patternId}")
    public ResponseEntity<CrossStitchPatternResponse> getPattern(
            @PathVariable UUID patternId,
            @AuthenticationPrincipal UUID userId
    ){
        CrossStitchPatternResponse result = crossStitchPatternService.getPattern(patternId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping
    public ResponseEntity<Page<BasicPatternInfo>> getAllPublicPatterns(Pageable pageable){
        Page<BasicPatternInfo> patterns = crossStitchPatternService.getPattersPage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(patterns);
    }
}
