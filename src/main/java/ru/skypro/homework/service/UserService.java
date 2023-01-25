package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.User;

@Service
@Slf4j
public class UserService {

    public NewPassword setPassword(NewPassword password) {
        log.info("Start UserService method setPassword");
        //userRepository
        return new NewPassword();
    }

    public User getUser_1() {
        log.info("Start UserService method getUser_1");
        //userRepository
        return new User();
    }

    public User updateUser(User user) {
        log.info("Start UserService method updateUser");
        //userRepository
        return new User();
    }

    public User updateUserImage(MultipartFile image) {
        log.info("Start UserService method updateUserImage");
        //userRepository
        return new User();
    }
}
