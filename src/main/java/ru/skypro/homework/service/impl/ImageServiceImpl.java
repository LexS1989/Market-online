package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final AuthService authService;

    public ImageServiceImpl(ImageRepository imageRepository,
                            AuthService authService) {
        this.imageRepository = imageRepository;
        this.authService = authService;
    }

    /**
     * method to create a new image for new ad
     *
     * @param ads - ad entity {@link Ads}
     * @param image - image file {@link MultipartFile}
     */
    @Override
    public void createImage(Ads ads, MultipartFile image) {
        log.info("Start ImageService method createImage");
        Image imageToDB = new Image();
        saveImagesToDatabase(ads, image, imageToDB);
    }

    /**
     * method for editing the image for ad in the database
     *
     * @param id - image number in the database
     * @param image - image file {@link MultipartFile}
     * @param authentication - data from the database about the user for authentication
     * @return - array byte
     */
    @Override
    public byte[] updateImage(int id, MultipartFile image, Authentication authentication) {
        log.info("Start ImageService method updateImage");
        Image imageToDB = getImage(id);

        Ads ads = imageToDB.getAds();
        String userName = ads.getUser().getEmail();
        authService.checkAccess(userName, authentication);
        saveImagesToDatabase(ads, image, imageToDB);

        return imageToDB.getData();
    }

    /**
     * method for saving the image to the database
     *
     * @param ads - ad entity {@link Ads}
     * @param fileImage - new image file {@link MultipartFile} uploaded new file
     * @param imageToDB - the entity image {@link Image} to write to the database, created or taken from the database
     * the repository method {@link ImageRepository#save(Object)} (Object)} is used
     */
    @Override
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

    /**
     * method for returning array byte from the database
     *
     * @param id - image number in the database
     * @return - array byte
     */
    @Override
    public byte[] getImageById(int id) {
        return getImage(id).getData();
    }

    /**
     * method for find image in the database
     *
     * @param id - image number in the database
     * the repository method {@link ImageRepository#findById(Object)} is used
     * @throws NotFoundException if image not found
     * @return - entity image from database {@link Image}
     */
    @Override
    public Image getImage(int id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }
}
