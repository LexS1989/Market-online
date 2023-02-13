package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdsService adsService;
    private final UserService userService;
    private final AuthService authService;

    public CommentService(AdsRepository adsRepository,
                          CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          AdsService adsService,
                          UserService userService,
                          AuthService authService) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.adsService = adsService;
        this.userService = userService;
        this.authService = authService;
    }

    public ResponseWrapperComment getAllCommentsForAd(int adPk) {
        log.info("Start AdsService method getAllCommentsForAd");
        adsService.getAds(adPk);
        List<Comment> allComments = commentRepository.findAllCommentByAdsId(adPk);

        ResponseWrapperComment result = new ResponseWrapperComment();
        result.setResults(commentMapper.ListCommentToListCommentDto(allComments));
        result.setCount(allComments.size());
        return result;
    }

    public CommentDto addComments(int adPk, CommentDto commentDto, Authentication authentication) {
        log.info("Start AdsService method addComments");
        Ads ads = adsRepository.findById(adPk).orElseThrow(() -> new NotFoundException());
        Comment newComment = commentMapper.commentDtoToComment(commentDto);
        newComment.setAds(ads);
        newComment.setUser(userService.findUser(authentication.getName()));
        newComment.setCreatedAt(LocalDateTime.now());
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }

    public CommentDto getCommentForAdByCommentId(int adPk, int id) {
        log.info("Start AdsService method getCommentForAdByCommentId");
        Comment foundComment = findCommentAd(adPk, id);
        return commentMapper.commentToCommentDto(foundComment);
    }

    public void deleteComments(int adPk, int id, Authentication authentication) {
        log.info("Start AdsService method deleteComments");
        Comment foundComment = findCommentAd(adPk, id);
        authService.checkAccess(foundComment.getUser().getEmail(),authentication);
        commentRepository.deleteById(id);
    }

    public CommentDto updateComments(int adPk, int id, CommentDto commentDto, Authentication authentication) {
        log.info("Start AdsService method updateComments");
        Comment foundComment = findCommentAd(adPk, id);
        authService.checkAccess(foundComment.getUser().getEmail(),authentication);

        Ads ads = foundComment.getAds();

        Comment newComment = commentMapper.commentDtoToComment(commentDto);

        newComment.setId(id);
        newComment.setAds(ads);
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setUser(userService.findUser(authentication.getName()));
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }

    private Comment findCommentAd(int adPk, int id) {
        return commentRepository.findCommentByAdsIdAndId(adPk, id)
                .orElseThrow(() -> new NotFoundException());
    }
}
