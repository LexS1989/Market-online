package ru.skypro.homework.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.Ads;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AdsMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(source = "user.id", target = "author")
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
//    TODO @Mapping(source = "images", target = "image") - разобраться с возвратом
    FullAdsDto AdsToFullAdsDto(Ads ads);

}
