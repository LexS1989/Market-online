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
import ru.skypro.homework.service.UserService;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserDetailsManager manager,
                           UserRepository userRepository,
                           UserService userService,
                           UserMapper userMapper) {
        this.manager = manager;
        this.encoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

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

    public void checkAccess(String userNameAuthor, Authentication authentication) {
        log.info("checkAccess - run method from service AuthService");
        ru.skypro.homework.entity.User foundUser = userService.findUser(authentication.getName());
        if (!foundUser.getRole().equals(Role.ADMIN) && !userNameAuthor.equals(foundUser.getEmail())) {
            log.info("forbidden");
            throw new NoPermissionException();
        }
        log.info("Verification completed, is the author or administrator of the site");
    }

}
