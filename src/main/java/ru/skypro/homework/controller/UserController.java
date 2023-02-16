package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/users")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class UserController {

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Operation(summary = "setPassword",
            tags = "Пользователи",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NewPassword.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = NewPassword.class))
                    ),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/set_password")
    public ResponseEntity<NewPassword> setPassword(@RequestBody NewPassword newPassword,
                                                   Authentication authentication) {
        log.info("Start UserController method setPassword");
        NewPassword result = userServiceImpl.setPassword(newPassword, authentication);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "getUser",
            tags = "Пользователи",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        log.info("Start UserController method getUser");
        return ResponseEntity.ok(userServiceImpl.getMyProfile(authentication.getName()));
    }

    @Operation(summary = "updateUser",
            tags = "Пользователи",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(responseCode = "204", content = @Content),
                    @ApiResponse(responseCode = "401", content = @Content),
                    @ApiResponse(responseCode = "403", content = @Content),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,
                                              Authentication authentication) {
        log.info("Start UserController method updateUser");
        UserDto updateUser = userServiceImpl.updateUser(userDto, authentication.getName());
        return ResponseEntity.ok(updateUser);
    }

    @Operation(summary = "updateUserImage",
            tags = "Пользователи",
            description = "updateUserImage",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestBody MultipartFile image,
                                                   Authentication authentication) {
        log.info("Start UserController method updateUserImage");
        userServiceImpl.updateUserAvatar(authentication.getName(), image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "updateUserImage",
            tags = "Пользователи",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)
                    ),
                    @ApiResponse(responseCode = "404", content = @Content),
            }
    )
    @GetMapping(value = "/me/images/{id}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getUserImage(@PathVariable int id) {
        return ResponseEntity.ok(userServiceImpl.getUserImage(id));
    }

}

