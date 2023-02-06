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

    public ResponseWrapperComment getAllCommentsForAd(String adPk) {
        log.info("Start AdsService method getAllCommentsForAd");
        Integer pk = Integer.parseInt(adPk);//TODO вынести парсинг в отдельный метод
        List<Comment> allComments = commentRepository.findAllCommentByAdsId(pk);
        if (allComments.isEmpty()) {
            return null;
        }
        ResponseWrapperComment result = new ResponseWrapperComment();
        //TODO попробовать сократить запись в ResponseWrapperComment через маппер
        result.setResults(commentMapper.ListCommentToListCommentDto(allComments));
        result.setCount(allComments.size());
        return result;
    }

    public CommentDto addComments(String adPk, CommentDto commentDto) {
        log.info("Start AdsService method addComments");
        Integer pk = Integer.parseInt(adPk);//TODO вынести парсинг в отдельный метод
        Ads ads = adsRepository.findById(pk).orElse(null);
        if (ads == null) {
            return null;
        }
        Comment newComment = commentMapper.commentDtoToComment(commentDto);
        newComment.setAds(ads);
        newComment.setUser(ads.getUser());//заглушка иначе код 500
        //TODO (автор коментария) временная заглушка, далее реализовать через авторизацию
        return commentMapper.commentToCommentDto(commentRepository.save(newComment));
    }

    public CommentDto getCommentForAdByCommentId(String adPk, int id) {
        // Вопрос: id - это id комментария, а "adPk" - это id объявления?
        // Или я неправильно понял, ведь тогда можно получить, удалить и изменить просто по id  комментария.
        log.info("Start AdsService method getCommentForAdByCommentId");
        Integer pk = Integer.parseInt(adPk);//TODO вынести парсинг в отдельный метод
        Comment findComment = commentRepository.findCommentByAdsIdAndId(pk, id);
        return commentMapper.commentToCommentDto(findComment);
    }

    public void deleteComments(String adPk, int id) {
        log.info("Start AdsService method deleteComments");
        Integer pk = Integer.parseInt(adPk);//TODO вынести парсинг в отдельный метод
        commentRepository.deleteById(id);// непонятно для чего adPk тогда нужен???
    }

    public CommentDto updateComments(String adPk, int id, CommentDto commentDto) {
        log.info("Start AdsService method updateComments");
        Integer pk = Integer.parseInt(adPk);//TODO вынести парсинг в отдельный метод
        Comment comment = commentRepository.findCommentByAdsIdAndId(pk, id);
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
