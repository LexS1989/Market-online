package ru.skypro.homework.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;
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
        return ResponseEntity.ok(userService.setPassword(newPassword));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        log.info("Start UserController method getUser");
        // метод в сервисе пока не написан
        // подумать как без передачи параметров получить данные о себе???
        return ResponseEntity.ok(userService.getUser_1());
    }

    @PatchMapping("/me")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.info("Start UserController method updateUser");
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateUserImage(@RequestBody MultipartFile image) {
        log.info("Start UserController method updateUserImage");
        return ResponseEntity.ok(userService.updateUserImage(image));
    }

}

