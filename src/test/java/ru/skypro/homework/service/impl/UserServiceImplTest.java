package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.NoPermissionException;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.mapper.UserMapperImpl;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AvatarRepository avatarRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private UserServiceImpl out;

    private User expectedUser;
    private UserDto expectedUserDto;
    private User testUserWithNotCorrectPassword;
    private Authentication auth;
    private NewPassword password;

    @BeforeEach
    void init() {
        password = new NewPassword();
        password.setCurrentPassword("Password");
        password.setNewPassword("NewPassword");

        expectedUser = new User();
        expectedUser.setId(17);
        expectedUser.setEmail("email@email.com");
        expectedUser.setPassword(password.getCurrentPassword());
        auth = new UsernamePasswordAuthenticationToken(expectedUser, null);

        expectedUserDto = new UserDto();
        expectedUserDto.setId(17);
        expectedUserDto.setEmail("email@email.com");
        auth = new UsernamePasswordAuthenticationToken(expectedUserDto, null);

        testUserWithNotCorrectPassword = new User();
        testUserWithNotCorrectPassword.setId(17);
        testUserWithNotCorrectPassword.setEmail("email@email.com");
        testUserWithNotCorrectPassword.setPassword("NotCorrectPassword");
        auth = new UsernamePasswordAuthenticationToken(testUserWithNotCorrectPassword, null);
    }

    @Test
    public void shouldReturnNewPasswordWhenExecuteSetPassword() {
        when(userRepository.findUserByEmailIgnoreCase(any(String.class)))
                .thenReturn(Optional.of(expectedUser));
        verify(userRepository, atMostOnce()).save(expectedUser);

        assertThat(out.setPassword(password, auth))
                .isEqualTo(password);
    }

    @Test
    public void shouldThrowNoPermissionExceptionWhenExecuteSetPassword() {
        when(userRepository.findUserByEmailIgnoreCase(any(String.class)))
                .thenReturn(Optional.of(testUserWithNotCorrectPassword));

        assertThatExceptionOfType(NoPermissionException.class)
                .isThrownBy(() -> out.setPassword(password, auth));

    }

    @Test
    public void shouldReturnUserDtoWhenExecuteGetMyProfile() {
        UserDto result = userMapper.userToUserDto(expectedUser);
        when(userRepository.findUserByEmailIgnoreCase(any(String.class)))
                .thenReturn(Optional.of(expectedUser));
        when(userMapper.userToUserDto(expectedUser))
                .thenReturn(result);

        assertThat(out.getMyProfile(expectedUserDto.getEmail()))
                .isEqualTo(expectedUserDto);
    }

    @Test
    public void shouldReturnUserDtoChangeWhenExecuteUpdateUser() {
        when(userRepository.findUserByEmailIgnoreCase(any(String.class)))
                .thenReturn(Optional.of(expectedUser));
        when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);

        UserDto result = out.updateUser(userMapper.userToUserDto(expectedUser), expectedUser.getEmail());

        assertThat(out.updateUser(expectedUserDto, expectedUserDto.getEmail()))
                .isEqualTo(result);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenFindUserByUserNameNotInDB() {
        when(userRepository.findUserByEmailIgnoreCase(any(String.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> out.findUser("Wrong email"));
    }
/*
    @Test
    void updateUserAvatar() {
    }

    @Test
    void getUserImage() {
    }*/
}