package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;

@RestController
@RequestMapping("/ads")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class AdsController {

    private final AdsService adsService;
    private final CommentService commentService;

    public AdsController(AdsService adsService, CommentService commentService) {
        this.adsService = adsService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapperAds> getALLAds() {
        log.info("Start AdsController method getAllAds");
        ResponseWrapperAds allAds = adsService.getAllAds();
        if (allAds == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allAds);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdsDto> addAds(@RequestPart(name = "properties") CreateAdsDto createAdsDto,
                                         @RequestPart MultipartFile image) {
        log.info("Start AdsController method addAds");
        return ResponseEntity.status(HttpStatus.CREATED).body(adsService.createAds(createAdsDto, image));
    }

    @GetMapping("/{ad_pk}/comments")
    public ResponseEntity<ResponseWrapperComment> getComments(@PathVariable(name = "ad_pk", required = true) String adPk) {
        log.info("Start AdsController method getComments");
        ResponseWrapperComment result = commentService.getAllCommentsForAd(adPk);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{ad_pk}/comments")
    public ResponseEntity<CommentDto> addComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                                  @RequestBody(required = true) CommentDto commentDto) {
        log.info("Start AdsController method addComments");
        CommentDto result = commentService.addComments(adPk, commentDto);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullAdsDto> getFullAd(@PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getFullAd");
        FullAdsDto fullAdsDto = adsService.getAds(id);
        if (fullAdsDto == null) {
            log.info("Empty");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fullAdsDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAds(@PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method removeAds");
        FullAdsDto adsRemove = adsService.getAds(id);
        if (adsRemove == null) {
            return ResponseEntity.notFound().build();
        }
        adsService.removeAds(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable(name = "id", required = true) int id,
                                            @RequestBody CreateAdsDto createAdsDto) {
        log.info("Start AdsController method updateAds");
        AdsDto result = adsService.updateAds(id, createAdsDto);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> getComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                                  @PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getComments");
        CommentDto result = commentService.getCommentForAdByCommentId(adPk, id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                               @PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method deleteComments");
        CommentDto commentDelete = commentService.getCommentForAdByCommentId(adPk, id);
        if (commentDelete == null) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteComments(adPk, id);//adPk нет смысла передавать
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> updateComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                                     @PathVariable(name = "id", required = true) int id,
                                                     @RequestBody CommentDto commentDto) {
        log.info("Start AdsController method updateComments");
        CommentDto result = commentService.updateComments(adPk, id, commentDto);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(@RequestParam(name = "authenticated", required = false) boolean authenticated,
                                                               @RequestParam(name = "authorities[0].authority", required = false) String authority,
                                                               @RequestParam(name = "credentials", required = false) Object credentials,
                                                               @RequestParam(name = "details", required = false) Object details,
                                                               @RequestParam(name = "principal", required = false) Object principal) {
        log.info("Start AdsController method getAdsMe");
        ResponseWrapperAds result = adsService.getAdsMe(authenticated, authority, credentials, details, principal);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
