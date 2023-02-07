package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;

import java.util.*;

@Service
@Slf4j
public class AdsService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final AdsMapper adsMapper;

    public AdsService(AdsRepository adsRepository,
                      CommentRepository commentRepository,
                      AdsMapper adsMapper) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.adsMapper = adsMapper;
    }

    public ResponseWrapperAds getAllAds() {
        log.info("Start AdsService method getAllAds");
        List<Ads> allAds = adsRepository.findAll();
        if (allAds.isEmpty()) {
            return null;
        }
        ResponseWrapperAds result = new ResponseWrapperAds();
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
