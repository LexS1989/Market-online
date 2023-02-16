package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.NoPermissionException;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarRepository avatarRepository;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.avatarRepository = avatarRepository;
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

    public void updateUserAvatar(String userName, MultipartFile image) {
        log.info("Start UserService method updateUserAvatar");

        User foundUser = findUser(userName);
        Avatar avatarUser = avatarRepository.findAvatarByUserEmailIgnoreCase(userName)
                .orElse(new Avatar());

        try {
            byte[] bytes = image.getBytes();
            avatarUser.setData(bytes);
        } catch (IOException e) {
            log.info("Avatar not loading");
            throw new RuntimeException(e);
        }
        avatarUser.setUser(foundUser);

        avatarRepository.save(avatarUser);
        log.info("Avatar changed");
    }

    public byte[] getUserImage(int id) {
        log.info("Start UserService method getUserImage");
        Avatar userAvatar = avatarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        return userAvatar.getData();
    }

    public User findUser(String userName) {
        return userRepository.findUserByEmailIgnoreCase(userName)
                .orElseThrow(() -> new NotFoundException());
    }
}
