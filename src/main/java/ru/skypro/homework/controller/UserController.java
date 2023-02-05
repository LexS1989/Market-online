package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(value = "http://localhost:3000")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/set_password")
    public ResponseEntity<NewPassword> setPassword(@RequestBody NewPassword newPassword) {
        log.info("Start UserController method setPassword");
        NewPassword result = userService.setPassword(newPassword);
        if (result == null) {
            return ResponseEntity.badRequest().build();//TODO временная заглушка
        }
        return ResponseEntity.ok(userService.setPassword(newPassword));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser() {
        log.info("Start UserController method getUser");
        //TODO реализовать авторизацию
        return ResponseEntity.ok(userService.getUser_1());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        log.info("Start UserController method updateUser");
        UserDto user = userService.updateUser(userDto);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUserImage(@RequestBody MultipartFile image) {
        log.info("Start UserController method updateUserImage");
        userService.updateUserAvatar(/*user,*/image);//TODO сделать вторым параметром "User" через авторизацию
        return ResponseEntity.ok().build();
    }

}

