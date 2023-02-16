package ru.skypro.homework.mapper;

import org.mapstruct.*;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AdsMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user.id", target = "author")
    @Mapping(source = "images", target = "image", qualifiedByName = "listImagesByteToListString")
    AdsDto adsToAdsDto(Ads ads);

    List<AdsDto> ListAdsToListAdsDto(List<Ads> ads);

    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "user.id")
    Ads adsDtoToAds(AdsDto adsDto);

    Ads createAdsDtoToAds(CreateAdsDto createAdsDto);

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user.firstName", target = "authorFirstName")
    @Mapping(source = "user.lastName", target = "authorLastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "images", target = "image", qualifiedByName = "listImagesByteToListString")
    FullAdsDto AdsToFullAdsDto(Ads ads);

    @Named("listImagesByteToListString")
    default List<String> listImagesByteToListString(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        List<String> listImage = new ArrayList<>();
        for (Image image : images) {
            listImage.add("/image/" + image.getId());
        }
        return listImage;
    }
}
