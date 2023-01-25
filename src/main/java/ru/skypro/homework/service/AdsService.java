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

    public Ads createAds(CreateAds createAds, MultipartFile image) {
        log.info("Start AdsService method createAds");
        //adsRepository
        return new Ads();
    }

    public ResponseWrapperComment getComments(String ad_pk) {
        log.info("Start AdsService method getComments");
        //adsRepository
        return new ResponseWrapperComment();
    }

    public Comment addComments(String ad_pk, Comment comment) {
        log.info("Start AdsService method addComments");
        //adsRepository
        return new Comment();
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

    public Ads updateAds(int id, CreateAds createAds) {
        log.info("Start AdsService method updateAds");
        //adsRepository
        return new Ads();
    }

    public Comment getComments_1(String ad_pk, int id) {
        log.info("Start AdsService method getComments_1");
        //adsRepository
        return new Comment();
    }

    public void deleteComments(String ad_pk, int id) {
        log.info("Start AdsService method deleteComments");
        //adsRepository
    }

    public Comment updateComments(String ad_pk, int id, Comment comment) {
        log.info("Start AdsService method updateComments");
        //adsRepository
        return new Comment();
    }

    public ResponseWrapperAds getAdsMe(boolean authenticated, String authority, Object credentials, Object details, Object principal) {
        log.info("Start AdsService method getAdsMe");
        //adsRepository
        return new ResponseWrapperAds();
    }
}
