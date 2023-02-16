package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.exceptions.NoPermissionException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserDetailsManager manager,
                           UserRepository userRepository,
                           UserServiceImpl userServiceImpl,
                           UserMapper userMapper) {
        this.manager = manager;
        this.encoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
        this.userServiceImpl = userServiceImpl;
        this.userMapper = userMapper;
    }

    /**
     * method for checking login and password
     *
     * @param userName - user email address
     * @param password - user password
     * @return - boolean result about profile access
     */
    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        return encoder.matches(password, encryptedPasswordWithoutEncryptionType);
    }

    /**
     * method for registering a new user
     *
     * @param registerReq - data required to register a new user in the database {@link RegisterReq}
     * @param role - user role {@link Role} on the site, when registering, by default it is assigned "User"
     * the repository method {@link UserRepository#save(Object)} is used
     * @return boolean result about registration
     */
    @Override
    public boolean register(RegisterReq registerReq, Role role) {
        log.info("registration");
        if (manager.userExists(registerReq.getUsername())) {
            return false;
        }
        manager.createUser(
                User.withDefaultPasswordEncoder()
                        .password(registerReq.getPassword())
                        .username(registerReq.getUsername())
                        .roles(role.name())
                        .build()
        );
        ru.skypro.homework.entity.
                User newUser = userMapper.registerReqToUser(registerReq);

        newUser.setCity("не указан");
        newUser.setRegDate(LocalDateTime.now());
        newUser.setRole(role);

        userRepository.save(newUser);
        log.info("registration completed successfully");
        return true;
    }

    /**
     * method to check if role and username match
     *
     * @param userNameAuthor - user email address
     * @param authentication - data from the database about the user for authentication
     * @throws NoPermissionException if the user does not have access rights
     */
    @Override
    public void checkAccess(String userNameAuthor, Authentication authentication) {
        log.info("checkAccess - run method from service AuthService");
        ru.skypro.homework.entity.User foundUser = userServiceImpl.findUser(authentication.getName());
        if (!foundUser.getRole().equals(Role.ADMIN) && !userNameAuthor.equals(foundUser.getEmail())) {
            log.info("forbidden");
            throw new NoPermissionException();
        }
        log.info("Verification completed, is the author or administrator of the site");
    }

}
