package ru.skypro.homework.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.AdsMapperImpl;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdsServiceImplTest {

    @Mock
    private UserServiceImpl userService;
    @Mock
    private ImageServiceImpl imageService;
    @Mock
    private AdsRepository adsRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Spy
    private AdsMapper adsMapper = new AdsMapperImpl();
    @InjectMocks
    private AdsServiceImpl out;

    private User expectedUser;
    private Authentication auth;
    private Image expectedImage;
    private CreateAdsDto createAdsDto;
    private Ads firstAds;
    private Ads secondAds;
    private List<Ads> adsList;
    private List<AdsDto> adsListDto;

    @BeforeEach
    void init() {
        expectedUser = new User();
        expectedUser.setId(17);
        expectedUser.setEmail("email@email.com");
        auth = new UsernamePasswordAuthenticationToken(expectedUser, null);

        expectedImage = new Image();
        expectedImage.setId(1);

        createAdsDto = new CreateAdsDto();
        createAdsDto.setDescription("Description");
        createAdsDto.setTitle("Title");
        createAdsDto.setPrice(1000);

        firstAds = new Ads();
        firstAds.setId(1);
        firstAds.setPrice(500);
        firstAds.setTitle("First Ads");
        firstAds.setUser(expectedUser);

        secondAds = new Ads();
        secondAds.setId(2);
        secondAds.setPrice(750);
        secondAds.setTitle("Second Ads");
        secondAds.setUser(expectedUser);

        adsList = new ArrayList<>();
        adsList.add(firstAds);
        adsList.add(secondAds);
    }

    @Test
    public void shouldReturnResponseWrapperAdsWithAllAdsWhenExecuteGetAllAds() {
        when(adsRepository.findAll())
                .thenReturn(adsList);
        ResponseWrapperAds result = out.getAllAds();

        assertThat(result.getCount()).isEqualTo(adsList.size());
        assertThat(result.getResults()).isNotEmpty();
    }

    @Test
    void createAds() {
        when(userService.findUser(any(String.class)))
                .thenReturn(expectedUser);
        Ads adsTest = adsMapper.createAdsDtoToAds(createAdsDto);
        when(adsRepository.save(any(Ads.class)))
                .thenReturn(adsTest);
        verify(imageService, atMostOnce()).createImage(any(), any());

        AdsDto result = out.createAds(createAdsDto, null, auth);

        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo(createAdsDto.getTitle());
        AssertionsForClassTypes.assertThat(result.getPrice()).isEqualTo(createAdsDto.getPrice());
    }

    @Test
    void getAds() {
    }

    @Test
    void removeAds() {
    }

    @Test
    void updateAds() {
    }

    @Test
    void getAdsMe() {
    }
}