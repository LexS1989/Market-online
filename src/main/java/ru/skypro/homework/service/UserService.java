package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.NoPermissionException;
import ru.skypro.homework.exceptions.NotFoundException;
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

    public NewPassword setPassword(NewPassword password, Authentication authentication) {
        log.info("Start UserService method setPassword");
        User foundUser = findUser(authentication.getName());
        if (!password.getCurrentPassword().equals(foundUser.getPassword())) {
            throw new NoPermissionException();
        }
        foundUser.setPassword(password.getNewPassword());
        userRepository.save(foundUser);
        return password;
    }

    public UserDto getMyProfile(String userName) {
        log.info("Start UserService method getMyProfile");
        return userMapper.userToUserDto(findUser(userName));
    }

    public UserDto updateUser(UserDto userDto, String userName) {
        log.info("Start UserService method updateUser");
        User foundUser = findUser(userName);
        foundUser.setFirstName(userDto.getFirstName());
        foundUser.setLastName(userDto.getLastName());
        foundUser.setPhone(userDto.getPhone());

        User result = userRepository.save(foundUser);

        return userMapper.userToUserDto(result);
    }

    public UserDto updateUserAvatar(String username, MultipartFile image) {
        log.info("Start UserService method updateUserAvatar");
        // TODO работа с картинками 5 неделя
        return new UserDto();
    }

    public User findUser(String userName) {
        return userRepository.findUserByEmailIgnoreCase(userName)
                .orElseThrow(() -> new NotFoundException());
    }
}
