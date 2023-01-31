package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdsService;

@RestController
@RequestMapping("/ads")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class AdsController {

    private final AdsService adsService;

    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapperAds> getALLAds() {
        log.info("Start AdsController method getAllAds");
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdsDto> addAds(@RequestPart(name = "properties") CreateAds createAds,
                                         @RequestPart MultipartFile image) {
        log.info("Start AdsController method addAds");
        return ResponseEntity.status(HttpStatus.CREATED).body(adsService.createAds(createAds, image));
    }

    @GetMapping("/{ad_pk}/comments")
    public ResponseEntity<ResponseWrapperComment> getComments(@PathVariable(name = "ad_pk", required = true) String adPk) {
        log.info("Start AdsController method getComments");
        return ResponseEntity.ok(adsService.getComments(adPk));
    }

    @PostMapping("/{ad_pk}/comments")
    public ResponseEntity<CommentDto> addComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                                  @RequestBody(required = true) CommentDto commentDto) {
        log.info("Start AdsController method addComments");
        return ResponseEntity.ok(adsService.addComments(adPk, commentDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullAds> getFullAd(@PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getFullAd");
        return ResponseEntity.ok(adsService.getAds(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAds(@PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method removeAds");
        /*FullAds adsRemove = adsService.getAds(id);
        if (adsRemove == null) {
            return ResponseEntity.notFound().build();
        }*/
        adsService.removeAds(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable(name = "id", required = true) int id,
                                            @RequestBody CreateAds createAds) {
        log.info("Start AdsController method updateAds");
        return ResponseEntity.ok(adsService.updateAds(id, createAds));
    }

    @GetMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> getComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                                  @PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getComments");
        return ResponseEntity.ok(adsService.getComments_1(adPk, id));
    }

    @DeleteMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                               @PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method deleteComments");
        /*Comment commentDelete = adsService.getComments_1(ad_pk, id);
        if (commentDelete == null) {
            return ResponseEntity.notFound().build();
        }*/
        adsService.deleteComments(adPk, id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> updateComments(@PathVariable(name = "ad_pk", required = true) String adPk,
                                                     @PathVariable(name = "id", required = true) int id,
                                                     @RequestBody CommentDto commentDto) {
        log.info("Start AdsController method updateComments");
        return ResponseEntity.ok(adsService.updateComments(adPk, id, commentDto));
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(@RequestParam(name = "authenticated", required = false) boolean authenticated,
                                                               @RequestParam(name = "authorities[0].authority", required = false) String authority,
                                                               @RequestParam(name = "credentials", required = false) Object credentials,
                                                               @RequestParam(name = "details", required = false) Object details,
                                                               @RequestParam(name = "principal", required = false) Object principal) {
        log.info("Start AdsController method getAdsMeUsingGET");
        return ResponseEntity.ok(adsService.getAdsMe(authenticated, authority, credentials, details, principal));
    }
}
