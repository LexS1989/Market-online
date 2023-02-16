package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;

public interface AdsService {

    ResponseWrapperAds getAllAds();

    AdsDto createAds(CreateAdsDto createAdsDto, MultipartFile image, Authentication authentication);

    FullAdsDto getAds(int id);

    @Transactional
    void removeAds(int id, Authentication authentication);

    AdsDto updateAds(int id, CreateAdsDto createAdsDto, Authentication authentication);

    ResponseWrapperAds getAdsMe(String userName);
}
