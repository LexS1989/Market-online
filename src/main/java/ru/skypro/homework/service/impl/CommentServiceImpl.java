package ru.skypro.homework.service.impl;

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
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdsServiceImpl adsServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final AuthService authService;

    public CommentServiceImpl(AdsRepository adsRepository,
                              CommentRepository commentRepository,
                              CommentMapper commentMapper,
                              AdsServiceImpl adsServiceImpl,
                              UserServiceImpl userServiceImpl,
                              AuthService authService) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.adsServiceImpl = adsServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.authService = authService;
    }

    /**
     * method to getting all the comments to the ad
     *
     * @param adPk - ad number in the database
     * the repository method {@link CommentRepository#findAllCommentByAdsId(int)} is used
     * @return - list of all ad comments {@link ResponseWrapperComment}
     */
    @Override
    public ResponseWrapperComment getAllCommentsForAd(int adPk) {
        log.info("Start AdsService method getAllCommentsForAd");
        adsServiceImpl.getAds(adPk);
        List<Comment> allComments = commentRepository.findAllCommentByAdsId(adPk);

        ResponseWrapperComment result = new ResponseWrapperComment();
        result.setResults(commentMapper.ListCommentToListCommentDto(allComments));
        result.setCount(allComments.size());
        return result;
    }

    /**
     * method to create a new comment
     *
     * @param adPk - ad number in the database
     * @param commentDto - comment entered by user {@link CommentDto}
     * @param authentication - data from the database about the user for authentication
     * the repository method {@link AdsRepository#findById(Object)} is used
     * the repository method {@link CommentRepository#save(Object)} is used
     * @return - new comment {@link CommentDto}
     */
    @Override
    public CommentDto addComments(int adPk, CommentDto commentDto, Authentication authentication) {
        log.info("Start AdsService method addComments");
        Ads ads = adsRepository.findById(adPk)
                .orElseThrow(() -> new NotFoundException());
        Comment newComment = commentMapper.commentDtoToComment(commentDto);
        newComment.setAds(ads);
        newComment.setUser(userServiceImpl.findUser(authentication.getName()));
        newComment.setCreatedAt(LocalDateTime.now());
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }

    /**
     * method for getting a comment to an ad
     *
     * @param adPk - ad number in the database
     * @param id - comment number in the database
     * @return - found comment {@link CommentDto}
     */
    @Override
    public CommentDto getCommentForAdByCommentId(int adPk, int id) {
        log.info("Start AdsService method getCommentForAdByCommentId");
        Comment foundComment = findCommentAd(adPk, id);
        return commentMapper.commentToCommentDto(foundComment);
    }

    /**
     * method for deleting a comment
     *
     * @param adPk - ad number in the database
     * @param id - comment number in the database
     * @param authentication - data from the database about the user for authentication
     * the repository method {@link CommentRepository#deleteById(Object)} is used
     */
    @Override
    public void deleteComments(int adPk, int id, Authentication authentication) {
        log.info("Start AdsService method deleteComments");
        Comment foundComment = findCommentAd(adPk, id);
        authService.checkAccess(foundComment.getUser().getEmail(),authentication);
        commentRepository.deleteById(id);
    }

    /**
     * method for editing the comment in the database
     *
     * @param adPk - ad number in the database
     * @param id - comment number in the database
     * @param commentDto - comment entered by user {@link CommentDto}
     * @param authentication - data from the database about the user for authentication
     * @return - modified comment {@link CommentDto}
     */
    @Override
    public CommentDto updateComments(int adPk, int id, CommentDto commentDto, Authentication authentication) {
        log.info("Start AdsService method updateComments");
        Comment foundComment = findCommentAd(adPk, id);
        authService.checkAccess(foundComment.getUser().getEmail(),authentication);

        Ads ads = foundComment.getAds();

        Comment newComment = commentMapper.commentDtoToComment(commentDto);

        newComment.setId(id);
        newComment.setAds(ads);
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setUser(userServiceImpl.findUser(authentication.getName()));
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }

    /**
     * method for searching the comment entity in the database
     *
     * @param adPk - ad number in the database
     * @param id - comment number in the database
     * the repository method {@link CommentRepository#findCommentByAdsIdAndId(int, int)} is used
     * @throws NotFoundException if comment not found
     * @return - the comment entity from the database {@link Comment}
     */
    private Comment findCommentAd(int adPk, int id) {
        return commentRepository.findCommentByAdsIdAndId(adPk, id)
                .orElseThrow(() -> new NotFoundException());
    }
}
