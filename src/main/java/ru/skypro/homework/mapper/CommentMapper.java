package ru.skypro.homework.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Comment;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user.id", target = "author")
    CommentDto commentToCommentDto(Comment comment);

    List<CommentDto> ListCommentToListCommentDto(List<Comment> comments);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "user.id")
    Comment commentDtoToComment(CommentDto commentDto);
}
