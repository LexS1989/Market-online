package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;

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
        //userRepository
        return new NewPassword();
    }

    public UserDto getUser_1() {
        log.info("Start UserService method getUser_1");
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

    public UserDto updateUserImage(MultipartFile image) {
        log.info("Start UserService method updateUserImage");
        //userRepository
        return new UserDto();
    }
}
