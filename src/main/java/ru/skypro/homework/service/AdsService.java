package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.NoPermissionException;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.*;

@Service
@Slf4j
public class AdsService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final AdsMapper adsMapper;
    private final UserRepository userRepository;

    public AdsService(AdsRepository adsRepository,
                      CommentRepository commentRepository,
                      AdsMapper adsMapper,
                      UserRepository userRepository) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.adsMapper = adsMapper;
        this.userRepository = userRepository;
    }

    public ResponseWrapperAds getAllAds() {
        log.info("Start AdsService method getAllAds");
        List<Ads> allAds = adsRepository.findAll();
        ResponseWrapperAds result = new ResponseWrapperAds();
        result.setCount(allAds.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAds));
        return result;
    }

    public AdsDto createAds(CreateAdsDto createAdsDto, MultipartFile image, Authentication authentication) {
        log.info("Start AdsService method createAds");
        User foundUser = userRepository.findUserByEmailIgnoreCase(authentication.getName()).orElseThrow(()-> new NotFoundException());
        Ads ads = adsMapper.createAdsDtoToAds(createAdsDto);
        ads.setUser(foundUser);
        //TODO отработать с image 5 неделя
        Ads result = adsRepository.save(ads);
        return adsMapper.adsToAdsDto(result);
    }

    public FullAdsDto getAds(int id) {
        log.info("Start AdsService method getAds");
        Ads ads = adsRepository.findById(id).orElseThrow(()->new NotFoundException());
        return adsMapper.AdsToFullAdsDto(ads);
    }

    @Transactional
    public void removeAds(int id, Authentication authentication) {
        log.info("Start AdsService method removeAds");
        String userNameAuthorAd = getAds(id).getEmail();
        checkAccess(userNameAuthorAd, authentication);
        List<Comment> findAllComments = commentRepository.findAllCommentByAdsId(id);
        if (!findAllComments.isEmpty()) {
            log.info("{} comments have been removed", findAllComments.size());
            commentRepository.deleteAllByAdsId(id);
        }
        adsRepository.deleteById(id);
    }

    public AdsDto updateAds(int id, CreateAdsDto createAdsDto, Authentication authentication) {
        log.info("Start AdsService method updateAds");
        String userNameAuthorAd = getAds(id).getEmail();
        checkAccess(userNameAuthorAd, authentication);

        Ads ads = adsRepository.findById(id).orElse(null);

        Ads updateAds = adsMapper.createAdsDtoToAds(createAdsDto);
        updateAds.setUser(ads.getUser());
        updateAds.setId(id);
        return adsMapper.adsToAdsDto(adsRepository.save(updateAds));
    }

    public ResponseWrapperAds getAdsMe(String userName) {
        log.info("Start AdsService method getAdsMe");

        List<Ads> allAdsAuthor = adsRepository.findAllAdsByUserEmailIgnoreCase(userName);

        ResponseWrapperAds result = new ResponseWrapperAds();
        result.setCount(allAdsAuthor.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAdsAuthor));
        return result;
    }

    //TODO метод наверно лучше будет перенести в AuthService
    public void checkAccess(String userNameAuthor, Authentication authentication) {
        //TODO попробовать прикрутить проверку по ролям
        if (!userNameAuthor.equals(authentication.getName())) {
            log.info("forbidden");
            throw new NoPermissionException();
        }
    }
}
