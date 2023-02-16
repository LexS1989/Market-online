package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.RegisterReq;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(source = "username", target = "email")
    User registerReqToUser(RegisterReq registerReq);

    @Mapping(source = "avatar", target = "image", qualifiedByName = "avatarByteToImageString")
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    @Named("avatarByteToImageString")
    default String avatarByteToImageString(Avatar avatar) {
        if (avatar != null) {
            return "/users/me/images/" + avatar.getId();
        }
        return null;
    }
}
