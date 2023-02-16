package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.ImageService;

@RestController
@RequestMapping("/image")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity <byte[]> getImage(@PathVariable int id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }
}
