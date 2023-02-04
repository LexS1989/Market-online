package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public NewPassword setPassword(NewPassword password) {
        log.info("Start UserService method setPassword");
        User getUser = userRepository.findUserById(1);//TODO заглушка, необходима авторизация
        if (!password.getCurrentPassword().equals(getUser.getPassword())) {
            return null;
        }
        getUser.setPassword(password.getNewPassword());
        userRepository.save(getUser);
        return password;
    }

    public UserDto getUser_1() {
        log.info("Start UserService method getUser_1");
        //TODO написать реализацию метода после авторизации
        //userRepository
        return new UserDto();
    }

    public UserDto updateUser(UserDto userDto) {
        log.info("Start UserService method updateUser");
        User user = userRepository.findUserById(userDto.getId());
        if (user == null) {
            return null;
        }
        user = userMapper.userDtoToUser(userDto);
        User result = userRepository.save(user);

        return userMapper.userToUserDto(result);
    }

    public UserDto updateUserAvatar(MultipartFile image) {
        log.info("Start UserService method updateUserAvatar");
        // TODO работа с картинками 5 неделя
        return new UserDto();
    }
}
