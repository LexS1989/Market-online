package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.util.List;

@Service
@Slf4j
public class CommentService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(AdsRepository adsRepository,
                          CommentRepository commentRepository,
                          CommentMapper commentMapper) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public ResponseWrapperComment getAllCommentsForAd(int adPk) {
        log.info("Start AdsService method getAllCommentsForAd");
        List<Comment> allComments = commentRepository.findAllCommentByAdsId(adPk);
        if (allComments.isEmpty()) {
            return null;
        }
        ResponseWrapperComment result = new ResponseWrapperComment();
        result.setResults(commentMapper.ListCommentToListCommentDto(allComments));
        result.setCount(allComments.size());
        return result;
    }

    public CommentDto addComments(int adPk, CommentDto commentDto) {
        log.info("Start AdsService method addComments");
        Ads ads = adsRepository.findById(adPk).orElse(null);
        if (ads == null) {
            return null;
        }
        Comment newComment = commentMapper.commentDtoToComment(commentDto);
        newComment.setAds(ads);
        newComment.setUser(ads.getUser());//заглушка иначе код 500
        //TODO (автор коментария) временная заглушка, далее реализовать через авторизацию
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }

    public CommentDto getCommentForAdByCommentId(int adPk, int id) {
        log.info("Start AdsService method getCommentForAdByCommentId");
        Comment findComment = commentRepository.findCommentByAdsIdAndId(adPk, id);
        return commentMapper.commentToCommentDto(findComment);
    }

    public void deleteComments(int id) {
        log.info("Start AdsService method deleteComments");
        commentRepository.deleteById(id);
    }

    public CommentDto updateComments(int adPk, int id, CommentDto commentDto) {
        log.info("Start AdsService method updateComments");
        Comment comment = commentRepository.findCommentByAdsIdAndId(adPk, id);
        if (comment == null) {
            return null;
        }
        Ads ads = comment.getAds();
        Comment newComment = commentMapper.commentDtoToComment(commentDto);
        newComment.setId(id);
        newComment.setAds(ads);
        newComment.setUser(ads.getUser());//заглушка иначе код 500
        //TODO (автор комментария) временная заглушка, далее реализовать через авторизацию
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }
}
