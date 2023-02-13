package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.repository.ImageRepository;

import java.io.IOException;

@Service
@Slf4j
public class ImageService {

    private final ImageRepository imageRepository;
    private final AuthService authService;

    public ImageService(ImageRepository imageRepository,
                        AuthService authService) {
        this.imageRepository = imageRepository;
        this.authService = authService;
    }

    public void createImage(Ads ads, MultipartFile image) {
        log.info("Start ImageService method createImage");

        Image imageForAd = new Image();

        try {
            // код, который кладет картинку в entity
            byte[] bytes = image.getBytes();
            imageForAd.setData(bytes);
        } catch (IOException e) {
            log.info("Image not loading");
            throw new RuntimeException(e);
        }
        imageForAd.setAds(ads);
        imageForAd.setFileSize(image.getSize());
        imageForAd.setMediaType(image.getContentType());
        // код сохранения картинки в БД
        imageRepository.save(imageForAd);
        log.info("Image changed");
    }


    public byte[] updateImage(int id, MultipartFile image, Authentication authentication) {
        log.info("Start ImageService method updateImage");
        // TODO пока не взаимодействует с фронтом, разобраться

        Image foundImage = imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());

        Ads ads = foundImage.getAds();

        String userName = ads.getUser().getEmail();
        authService.checkAccess(userName, authentication);

        try {
            // код, который кладет картинку в entity
            byte[] bytes = image.getBytes();
            foundImage.setData(bytes);
        } catch (IOException e) {
            log.info("Image not loading");
            throw new RuntimeException(e);
        }
        foundImage.setAds(ads);
        foundImage.setFileSize(image.getSize());
        foundImage.setMediaType(image.getContentType());

        imageRepository.save(foundImage);
        log.info("Image changed");

        return foundImage.getData();
    }
}
