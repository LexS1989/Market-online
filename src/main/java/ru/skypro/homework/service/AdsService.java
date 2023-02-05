package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.util.*;

@Service
@Slf4j
public class AdsService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final AdsMapper adsMapper;
    private final CommentMapper commentMapper;

    public AdsService(AdsRepository adsRepository,
                      CommentRepository commentRepository,
                      CommentMapper commentMapper,
                      AdsMapper adsMapper) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.adsMapper = adsMapper;
        this.commentMapper = commentMapper;
    }

    public ResponseWrapperAds getAllAds() {
        log.info("Start AdsService method getAllAds");
        List<Ads> allAds = adsRepository.findAll();
        if (allAds.isEmpty()) {
            return null;
        }
        ResponseWrapperAds result = new ResponseWrapperAds();
        //TODO попробовать сократить запись в ResponseWrapperAds через маппер
        result.setCount(allAds.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAds));
        return result;
    }

    public AdsDto createAds(CreateAdsDto createAdsDto, MultipartFile image) {
        log.info("Start AdsService method createAds");
        Ads ads = adsMapper.createAdsDtoToAds(createAdsDto);
        //TODO отработать с image 5 неделя
        Ads result = adsRepository.save(ads);
        return adsMapper.adsToAdsDto(result);
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

    public FullAdsDto getAds(int id) {
        log.info("Start AdsService method getAds");
        Ads ads = adsRepository.findById(id).orElse(null);
        if (ads != null) {
            return adsMapper.AdsToFullAdsDto(ads);
        }
        return null;
    }

    @Transactional
    public void removeAds(int id) {
        log.info("Start AdsService method removeAds");
        List<Comment> findAllComments = commentRepository.findAllCommentByAdsId(id);
        if (!findAllComments.isEmpty()) {
            log.info("{} comments have been removed", findAllComments.size());
            commentRepository.deleteAllByAdsId(id);
        }
        adsRepository.deleteById(id);
    }

    public AdsDto updateAds(int id, CreateAdsDto createAdsDto) {
        log.info("Start AdsService method updateAds");
        Ads ads = adsRepository.findById(id).orElse(null);
        if (ads == null) {
            return null;
        }
        Ads updateAds = adsMapper.createAdsDtoToAds(createAdsDto);
        updateAds.setUser(ads.getUser());
        updateAds.setId(id);
        return adsMapper.adsToAdsDto(adsRepository.save(updateAds));
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

    public ResponseWrapperAds getAdsMe(boolean authenticated,
                                       String authority,
                                       Object credentials,
                                       Object details,
                                       Object principal) {
        log.info("Start AdsService method getAdsMe");
        int id = 2;// TODO строка для тестирования работы метода, далее необходимо реализовать авторизацию
        List<Ads> allAdsAuthor = adsRepository.findAllAdsByUserId(id);
        if (allAdsAuthor.isEmpty()) {
            return null;
        }
        ResponseWrapperAds result = new ResponseWrapperAds();
        result.setCount(allAdsAuthor.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAdsAuthor));
        return result;
    }
}
