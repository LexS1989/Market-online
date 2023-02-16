package ru.skypro.homework.service;

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

import java.util.*;

@Service
@Slf4j
public class AdsService {

    private final AdsRepository adsRepository;
    private final CommentRepository commentRepository;
    private final AdsMapper adsMapper;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserService userService;
    private final AuthService authService;
    private final ImageRepository imageRepository;


    public AdsService(AdsRepository adsRepository,
                      CommentRepository commentRepository,
                      AdsMapper adsMapper,
                      UserRepository userRepository,
                      ImageService imageService,
                      UserService userService,
                      AuthService authService,
                      ImageRepository imageRepository) {
        this.adsRepository = adsRepository;
        this.commentRepository = commentRepository;
        this.adsMapper = adsMapper;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.userService = userService;
        this.authService = authService;
        this.imageRepository = imageRepository;
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
        User foundUser = userService.findUser(authentication.getName());
        Ads ads = adsMapper.createAdsDtoToAds(createAdsDto);
        ads.setUser(foundUser);
        Ads result = adsRepository.save(ads);
        imageService.createImage(result, image);
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

    public ResponseWrapperAds getAdsMe(String userName) {
        log.info("Start AdsService method getAdsMe");
        List<Ads> allAdsAuthor = adsRepository.findAllAdsByUserEmailIgnoreCase(userName);

        ResponseWrapperAds result = new ResponseWrapperAds();
        result.setCount(allAdsAuthor.size());
        result.setResults(adsMapper.ListAdsToListAdsDto(allAdsAuthor));
        return result;
    }
}
