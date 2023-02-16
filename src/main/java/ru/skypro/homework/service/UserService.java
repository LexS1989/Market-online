package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;

public interface UserService {

    NewPassword setPassword(NewPassword password, Authentication authentication);

    UserDto getMyProfile(String userName);

    UserDto updateUser(UserDto userDto, String userName);

    void updateUserAvatar(String userName, MultipartFile image);

    byte[] getUserImage(int id);

    User findUser(String userName);
}
