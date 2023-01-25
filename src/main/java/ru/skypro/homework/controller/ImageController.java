package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.service.ImageService;

import java.util.Collection;

@RestController
@RequestMapping("/image")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Collection<byte[]>> updateAdsImage(@PathVariable(name = "id", required = true) int id,
                                                             @RequestBody MultipartFile image) {
        log.info("Start ImageController method updateAdsImage");
        return ResponseEntity.ok(imageService.updateImage(id, image));
    }
}
