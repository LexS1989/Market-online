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
        Image imageToDB = new Image();
        saveImagesToDatabase(ads, image, imageToDB);
    }

    public byte[] updateImage(int id, MultipartFile image, Authentication authentication) {
        log.info("Start ImageService method updateImage");
        Image imageToDB = getImage(id);

        Ads ads = imageToDB.getAds();
        String userName = ads.getUser().getEmail();
        authService.checkAccess(userName, authentication);
        saveImagesToDatabase(ads, image, imageToDB);

        return imageToDB.getData();
    }

    public void saveImagesToDatabase(Ads ads, MultipartFile fileImage, Image imageToDB) {
        try {

            byte[] bytes = fileImage.getBytes();
            imageToDB.setData(bytes);
        } catch (IOException e) {
            log.info("Image not loading");
            throw new RuntimeException(e);
        }
        imageToDB.setAds(ads);
        imageToDB.setFileSize(fileImage.getSize());
        imageToDB.setMediaType(fileImage.getContentType());

        imageRepository.save(imageToDB);
        log.info("Image changed");
    }

    public byte[] getImageById(int id) {
        return getImage(id).getData();
    }

    public Image getImage(int id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }
}
