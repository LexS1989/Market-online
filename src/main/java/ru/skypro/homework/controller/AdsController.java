package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdsDto> addAds(@RequestPart(name = "properties") CreateAdsDto createAdsDto,
                                         @RequestPart MultipartFile image,
                                         Authentication authentication) {
        log.info("Start AdsController method addAds");
        return ResponseEntity.status(HttpStatus.CREATED).body(adsService.createAds(createAdsDto, image, authentication));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ad_pk}/comments")
    public ResponseEntity<ResponseWrapperComment> getComments(@PathVariable(name = "ad_pk", required = true) int adPk) {
        log.info("Start AdsController method getComments");
        return ResponseEntity.ok(commentService.getAllCommentsForAd(adPk));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{ad_pk}/comments")
    public ResponseEntity<CommentDto> addComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                                  @RequestBody(required = true) CommentDto commentDto,
                                                  Authentication authentication) {
        log.info("Start AdsController method addComments");
        return ResponseEntity.ok(commentService.addComments(adPk, commentDto, authentication));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<FullAdsDto> getFullAd(@PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getFullAd");
        FullAdsDto fullAdsDto = adsService.getAds(id);
        return ResponseEntity.ok(fullAdsDto);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAds(@PathVariable(name = "id", required = true) int id,
                                          Authentication authentication) {
        log.info("Start AdsController method removeAds");
        adsService.removeAds(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable(name = "id", required = true) int id,
                                            @RequestBody CreateAdsDto createAdsDto,
                                            Authentication authentication) {
        log.info("Start AdsController method updateAds");
        AdsDto result = adsService.updateAds(id, createAdsDto, authentication);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> getComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                                  @PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getComments");
        return ResponseEntity.ok(commentService.getCommentForAdByCommentId(adPk, id));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                               @PathVariable(name = "id", required = true) int id,
                                               Authentication authentication) {
        log.info("Start AdsController method deleteComments");
        commentService.deleteComments(adPk, id, authentication);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> updateComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                                     @PathVariable(name = "id", required = true) int id,
                                                     @RequestBody CommentDto commentDto,
                                                     Authentication authentication) {
        log.info("Start AdsController method updateComments");
        CommentDto result = commentService.updateComments(adPk, id, commentDto, authentication);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(Authentication authentication) {
        log.info("Start AdsController method getAdsMe");
        ResponseWrapperAds result = adsService.getAdsMe(authentication.getName());
        // Статус 404 убрал, т.к. если у пользователя нет ни одного объявления, то заходя в профиль, фронт падает.
        // В спецификации 404 есть, как это обработать???, или вообще убрать???
        return ResponseEntity.ok(result);
    }
}
