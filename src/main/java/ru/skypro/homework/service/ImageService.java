package ru.skypro.homework.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class ImageService {

    public Collection<byte[]> updateImage(int id, MultipartFile image) {
        log.info("Start ImageService method updateImage");
        //imageRepository поиск в базе
        byte[] imageNew = null;
        try {
            imageNew = image.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageRepository перезапись
        List<byte[]> result = new ArrayList<>();
        result.add(imageNew);
        return result;
    }
}
