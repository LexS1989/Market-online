package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapperComment;

public interface CommentService {

    ResponseWrapperComment getAllCommentsForAd(int adPk);

    CommentDto addComments(int adPk, CommentDto commentDto, Authentication authentication);

    CommentDto getCommentForAdByCommentId(int adPk, int id);

    void deleteComments(int adPk, int id, Authentication authentication);

    CommentDto updateComments(int adPk, int id, CommentDto commentDto, Authentication authentication);
}
