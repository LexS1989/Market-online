package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.AuthService;

import java.util.*;

@Service
@Slf4j
public class AdsServiceImpl implements AdsService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final AdsMapper adsMapper;
    private final UserRepository userRepository;
    private final ImageServiceImpl imageServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final AuthService authService;
    private final ImageRepository imageRepository;


    public AdsServiceImpl(AdsRepository adsRepository,
                          CommentRepository commentRepository,
                          AdsMapper adsMapper,
                          UserRepository userRepository,
                          ImageServiceImpl imageServiceImpl,
                          UserServiceImpl userServiceImpl,
                          AuthService authService,
                          ImageRepository imageRepository) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.adsMapper = adsMapper;
        this.userRepository = userRepository;
        this.imageServiceImpl = imageServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.authService = authService;
        this.imageRepository = imageRepository;
    }

    /**
     * method to returns all ads from the database
     *
     * the repository method {@link AdsRepository#findAll()} is used
     * @return - list of all ads from the database {@link ResponseWrapperAds}
     */
    @Override
    public ResponseWrapperAds getAllAds() {
        log.info("Start AdsService method getAllAds");
        List<Ads> allAds = adsRepository.findAll();
        ResponseWrapperAds result = new ResponseWrapperAds();
        result.setCount(allAds.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAds));
        return result;
    }

    /**
     * method to create a new ad
     *
     * @param createAdsDto - data entered by the user {@link CreateAdsDto}
     * @param image - image file {@link MultipartFile}
     * @param authentication - data from the database about the user for authentication
     * the repository method {@link AdsRepository#save(Object)} is used
     * @return - new ad {@link AdsDto}
     */
    @Override
    public AdsDto createAds(CreateAdsDto createAdsDto, MultipartFile image, Authentication authentication) {
        log.info("Start AdsService method createAds");
        User foundUser = userServiceImpl.findUser(authentication.getName());
        Ads ads = adsMapper.createAdsDtoToAds(createAdsDto);
        ads.setUser(foundUser);
        Ads result = adsRepository.save(ads);
        imageServiceImpl.createImage(result, image);
        return adsMapper.adsToAdsDto(result);
    }

    /**
     * method for getting the ad from the database
     *
     * @param id - ad number in the database
     * the repository method {@link AdsRepository#findById(Object)} is used
     * @throws NotFoundException if ad not found
     * @return - full description of the ad from the database {@link FullAdsDto}
     */
    @Override
    public FullAdsDto getAds(int id) {
        log.info("Start AdsService method getAds");
        Ads ads = adsRepository.findById(id)
                .orElseThrow(()->new NotFoundException());
        return adsMapper.AdsToFullAdsDto(ads);
    }

    /**
     * method for removing the ad from the database
     *
     * @param id - ad number in the database
     * @param authentication - data from the database about the user for authentication
     * the repository method {@link CommentRepository#findAllCommentByAdsId(int)} is used
     * the repository method {@link CommentRepository#deleteAllByAdsId(int)} is used
     * the repository method {@link ImageRepository#findAllImageByAdsId(int)} is used
     * the repository method {@link ImageRepository#deleteAllByAdsId(int)} is used
     * the repository method {@link AdsRepository#deleteById(Object)} is used
     */
    @Override
    @Transactional
    public void removeAds(int id, Authentication authentication) {
        log.info("Start AdsService method removeAds");
        String userNameAuthorAd = getAds(id).getEmail();
        authService.checkAccess(userNameAuthorAd, authentication);
        List<Comment> findAllComments = commentRepository.findAllCommentByAdsId(id);
        if (!findAllComments.isEmpty()) {
            log.info("{} comments have been removed", findAllComments.size());
            commentRepository.deleteAllByAdsId(id);
        }
        List<Image> findAllImageAd = imageRepository.findAllImageByAdsId(id);
        if (!findAllImageAd.isEmpty()) {
            log.info("{} images from ad removed", findAllImageAd.size());
            imageRepository.deleteAllByAdsId(id);
        }
        adsRepository.deleteById(id);
        log.info("Ad removal complete");
    }

    /**
     * method for editing the ad in the database
     *
     * @param id - ad number in the database
     * @param createAdsDto - data entered by the user {@link CreateAdsDto}
     * @param authentication - data from the database about the user for authentication
     * the repository method {@link AdsRepository#findById(Object)} is used
     * the repository method {@link AdsRepository#save(Object)} is used
     * @throws NotFoundException if ad not found
     * @return - modified ad {@link AdsDto}
     */
    @Override
    public AdsDto updateAds(int id, CreateAdsDto createAdsDto, Authentication authentication) {
        log.info("Start AdsService method updateAds");
        Ads ads = adsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        String userNameAuthorAd = ads.getUser().getEmail();
        authService.checkAccess(userNameAuthorAd, authentication);

        Ads updateAds = adsMapper.createAdsDtoToAds(createAdsDto);
        updateAds.setUser(ads.getUser());
        updateAds.setId(id);
        return adsMapper.adsToAdsDto(adsRepository.save(updateAds));
    }

    /**
     * a method for getting its ads
     *
     * @param userName - user email address
     * @return - list of all user ads from the database {@link ResponseWrapperAds}
     */
    @Override
    public ResponseWrapperAds getAdsMe(String userName) {
        log.info("Start AdsService method getAdsMe");
        List<Ads> allAdsAuthor = adsRepository.findAllAdsByUserEmailIgnoreCase(userName);

        ResponseWrapperAds result = new ResponseWrapperAds();
        result.setCount(allAdsAuthor.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAdsAuthor));
        return result;
    }
}
