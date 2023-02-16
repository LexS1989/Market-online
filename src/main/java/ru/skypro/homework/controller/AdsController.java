package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.CommentServiceImpl;
import ru.skypro.homework.service.impl.ImageServiceImpl;

@RestController
@RequestMapping("/ads")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class AdsController {

    private final AdsServiceImpl adsServiceImpl;
    private final CommentServiceImpl commentServiceImpl;
    private final ImageServiceImpl imageServiceImpl;

    public AdsController(AdsServiceImpl adsServiceImpl,
                         CommentServiceImpl commentServiceImpl,
                         ImageServiceImpl imageServiceImpl) {
        this.adsServiceImpl = adsServiceImpl;
        this.commentServiceImpl = commentServiceImpl;
        this.imageServiceImpl = imageServiceImpl;
    }

    @Operation(summary = "getALLAds",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseWrapperAds.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ResponseWrapperAds> getALLAds() {
        log.info("Start AdsController method getAllAds");
        return ResponseEntity.ok(adsServiceImpl.getAllAds());
    }

    @Operation(summary = "addAds",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = AdsDto.class))
                    ),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdsDto> addAds(@RequestPart(name = "properties") CreateAdsDto createAdsDto,
                                         @RequestPart MultipartFile image,
                                         Authentication authentication) {
        log.info("Start AdsController method addAds");
        return ResponseEntity.status(HttpStatus.CREATED).body(adsServiceImpl.createAds(createAdsDto, image, authentication));
    }

    @Operation(summary = "getComments",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseWrapperComment.class))
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ad_pk}/comments")
    public ResponseEntity<ResponseWrapperComment> getComments(@PathVariable(name = "ad_pk", required = true) int adPk) {
        log.info("Start AdsController method getComments");
        return ResponseEntity.ok(commentServiceImpl.getAllCommentsForAd(adPk));
    }

    @Operation(summary = "addComments",
            tags = "Объявления",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CommentDto.class))
                    ),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{ad_pk}/comments")
    public ResponseEntity<CommentDto> addComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                                  @RequestBody(required = true) CommentDto commentDto,
                                                  Authentication authentication) {
        log.info("Start AdsController method addComments");
        return ResponseEntity.ok(commentServiceImpl.addComments(adPk, commentDto, authentication));
    }

    @Operation(summary = "getFullAd",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = FullAdsDto.class))
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<FullAdsDto> getFullAd(@PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getFullAd");
        FullAdsDto fullAdsDto = adsServiceImpl.getAds(id);
        return ResponseEntity.ok(fullAdsDto);
    }

    @Operation(summary = "removeAds",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "204", content = @Content),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content)
            }
    )
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAds(@PathVariable(name = "id", required = true) int id,
                                          Authentication authentication) {
        log.info("Start AdsController method removeAds");
        adsServiceImpl.removeAds(id, authentication);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "updateAds",
            tags = "Объявления",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateAdsDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = AdsDto.class))
                    ),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable(name = "id", required = true) int id,
                                            @RequestBody CreateAdsDto createAdsDto,
                                            Authentication authentication) {
        log.info("Start AdsController method updateAds");
        AdsDto result = adsServiceImpl.updateAds(id, createAdsDto, authentication);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "getComments",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CommentDto.class))
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> getComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                                  @PathVariable(name = "id", required = true) int id) {
        log.info("Start AdsController method getComments");
        return ResponseEntity.ok(commentServiceImpl.getCommentForAdByCommentId(adPk, id));
    }


    @Operation(summary = "deleteComments",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                               @PathVariable(name = "id", required = true) int id,
                                               Authentication authentication) {
        log.info("Start AdsController method deleteComments");
        commentServiceImpl.deleteComments(adPk, id, authentication);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "updateComments",
            tags = "Объявления",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CommentDto.class))
                    ),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{ad_pk}/comments/{id}")
    public ResponseEntity<CommentDto> updateComments(@PathVariable(name = "ad_pk", required = true) int adPk,
                                                     @PathVariable(name = "id", required = true) int id,
                                                     @RequestBody CommentDto commentDto,
                                                     Authentication authentication) {
        log.info("Start AdsController method updateComments");
        CommentDto result = commentServiceImpl.updateComments(adPk, id, commentDto, authentication);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "getAdsMe",
            tags = "Объявления",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseWrapperAds.class))
                    ),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAdsMe(Authentication authentication) {
        log.info("Start AdsController method getAdsMe");
        ResponseWrapperAds result = adsServiceImpl.getAdsMe(authentication.getName());
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "updateAdsImage",
            tags = "Объявления",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = byte[].class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateAdsImage(@PathVariable(name = "id", required = true) int id,
                                                 @RequestBody MultipartFile image,
                                                 Authentication authentication) {
        log.info("Start AdsController method updateAdsImage");
        return ResponseEntity.ok(imageServiceImpl.updateImage(id, image, authentication));
    }
}
