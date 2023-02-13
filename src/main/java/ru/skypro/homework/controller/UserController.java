package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/set_password")
    public ResponseEntity<NewPassword> setPassword(@RequestBody NewPassword newPassword,
                                                   Authentication authentication) {
        log.info("Start UserController method setPassword");
        NewPassword result = userService.setPassword(newPassword, authentication);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        log.info("Start UserController method getUser");
        return ResponseEntity.ok(userService.getMyProfile(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,
                                              Authentication authentication) {
        log.info("Start UserController method updateUser");
        UserDto updateUser = userService.updateUser(userDto, authentication.getName());
        return ResponseEntity.ok(updateUser);
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestBody MultipartFile image,
                                                   Authentication authentication) {
        log.info("Start UserController method updateUserImage");
        userService.updateUserAvatar(authentication.getName(), image);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/me/images/{id}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getUserImage(Authentication authentication) {
        //TODO не возвращает картинку по указанному URL во FROTEND разобраться позже.
        //в базу сохранение есть, в свагере достать могу, разобраться с фронтом, нет отображения в профиле.
        return ResponseEntity.ok(userService.getUserImage(authentication));
    }

}

