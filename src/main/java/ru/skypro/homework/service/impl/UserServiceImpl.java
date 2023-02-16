package ru.skypro.homework.service.impl;

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
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarRepository avatarRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.avatarRepository = avatarRepository;
    }

    /**
     * Password change method
     *
     * @param password - object contains new and old passwords {@link NewPassword}
     * @param authentication - data from the database about the user for authentication
     * the repository method {@link UserRepository#save(Object)} is used
     * @throws NoPermissionException if an incorrect password is entered
     * @return - update password {@link NewPassword}
     */
    @Override
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

    /**
     * method for get data about your profile
     *
     * @param userName - user email address
     * @return - profile user {@link UserDto}
     */
    @Override
    public UserDto getMyProfile(String userName) {
        log.info("Start UserService method getMyProfile");
        return userMapper.userToUserDto(findUser(userName));
    }

    /**
     * method for editing the user in the database
     *
     * @param userDto - user edit data {@link UserDto}
     * @param userName  - user email address
     * the repository method {@link UserRepository#save(Object)} is used
     * @return - modified user {@link UserDto}
     */
    @Override
    public UserDto updateUser(UserDto userDto, String userName) {
        log.info("Start UserService method updateUser");
        User foundUser = findUser(userName);
        foundUser.setFirstName(userDto.getFirstName());
        foundUser.setLastName(userDto.getLastName());
        foundUser.setPhone(userDto.getPhone());

        User result = userRepository.save(foundUser);
        return userMapper.userToUserDto(result);
    }

    /**
     * method to edit user profile avatar
     *
     * @param userName  - user email address
     * @param image - image file {@link MultipartFile}
     * the repository method {@link AvatarRepository#findAvatarByUserEmailIgnoreCase(String)} is used
     * the repository method {@link AvatarRepository#save(Object)} is used
     */
    @Override
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

    /**
     * method to find user avatar in the database
     *
     * @param id - user number in the database
     * the repository method {@link AvatarRepository#findById(Object)} is used
     * @throws NotFoundException if user avatar not found
     * @return - array byte
     */
    @Override
    public byte[] getUserImage(int id) {
        log.info("Start UserService method getUserImage");
        Avatar userAvatar = avatarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        return userAvatar.getData();
    }

    /**
     * get for a user in the database by email address
     *
     * @param userName - user email address
     * the repository method {@link UserRepository#findUserByEmailIgnoreCase(String)} is used
     * @throws NotFoundException if user not found
     * @return - entity {@link User} from the database
     */
    @Override
    public User findUser(String userName) {
        return userRepository.findUserByEmailIgnoreCase(userName)
                .orElseThrow(() -> new NotFoundException());
    }
}
