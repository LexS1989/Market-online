package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

@Service
@Slf4j
public class AdsService {

    public ResponseWrapperAds getAllAds() {
        log.info("Start AdsService method getAllAds");
        //adsRepository
        return new ResponseWrapperAds();
    }

    public AdsDto createAds(CreateAds createAds, MultipartFile image) {
        log.info("Start AdsService method createAds");
        //adsRepository
        return new AdsDto();
    }

    public ResponseWrapperComment getComments(String adPk) {
        log.info("Start AdsService method getComments");
        //adsRepository
        return new ResponseWrapperComment();
    }

    public CommentDto addComments(String adPk, CommentDto commentDto) {
        log.info("Start AdsService method addComments");
        //adsRepository
        return new CommentDto();
    }

    public FullAds getAds(int id) {
        log.info("Start AdsService method getAds");
        //adsRepository
        return new FullAds();
    }

    public void removeAds(int id) {
        log.info("Start AdsService method removeAds");
        //adsRepository.deleteById(id);
    }

    public AdsDto updateAds(int id, CreateAds createAds) {
        log.info("Start AdsService method updateAds");
        //adsRepository
        return new AdsDto();
    }

    public CommentDto getComments_1(String adPk, int id) {
        log.info("Start AdsService method getComments_1");
        //adsRepository
        return new CommentDto();
    }

    public void deleteComments(String adPk, int id) {
        log.info("Start AdsService method deleteComments");
        //adsRepository
    }

    public CommentDto updateComments(String adPk, int id, CommentDto commentDto) {
        log.info("Start AdsService method updateComments");
        //adsRepository
        return new CommentDto();
    }

    public ResponseWrapperAds getAdsMe(boolean authenticated, String authority, Object credentials, Object details, Object principal) {
        log.info("Start AdsService method getAdsMe");
        //adsRepository
        return new ResponseWrapperAds();
    }
}
