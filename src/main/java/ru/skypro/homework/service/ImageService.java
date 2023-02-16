package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;

public interface ImageService {

    void createImage(Ads ads, MultipartFile image);

    byte[] updateImage(int id, MultipartFile image, Authentication authentication);

    void saveImagesToDatabase(Ads ads, MultipartFile fileImage, Image imageToDB);

    byte[] getImageById(int id);

    Image getImage(int id);
}
