package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.impl.ImageServiceImpl;

@RestController
@RequestMapping("/image")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class ImageController {

    private final ImageServiceImpl imageServiceImpl;

    public ImageController(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;
    }

    @Operation(summary = "getImage",
            tags = "Изображения",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = byte.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        return ResponseEntity.ok(imageServiceImpl.getImageById(id));
    }
}
