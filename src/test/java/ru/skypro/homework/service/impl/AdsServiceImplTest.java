package ru.skypro.homework.service.impl;

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
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exceptions.NotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.AdsMapperImpl;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdsServiceImplTest {

    @Mock
    private UserServiceImpl userService;
    @Mock
    private ImageServiceImpl imageService;
    @Mock
    private AuthServiceImpl authService;
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
    private CreateAdsDto createAdsDto;
    private Ads firstAds;
    private Ads secondAds;
    private List<Ads> adsList;

    @BeforeEach
    void init() {
        expectedUser = new User();
        expectedUser.setId(17);
        expectedUser.setFirstName("Lex");
        expectedUser.setEmail("email@email.com");
        auth = new UsernamePasswordAuthenticationToken(expectedUser, null);

        createAdsDto = new CreateAdsDto();
        createAdsDto.setDescription("Description");
        createAdsDto.setTitle("Title");
        createAdsDto.setPrice(1000);

        firstAds = new Ads();
        firstAds.setId(1);
        firstAds.setTitle("First Ads");
        firstAds.setPrice(500);
        firstAds.setUser(expectedUser);

        secondAds = new Ads();
        secondAds.setId(2);
        secondAds.setTitle("Second Ads");
        secondAds.setPrice(750);
        secondAds.setUser(expectedUser);

        adsList = new ArrayList<>();
        adsList.add(firstAds);
        adsList.add(secondAds);
    }

    @Test
    public void getAllAds_ShouldReturnResponseWrapperAdsWithAllAds() {
        when(adsRepository.findAll())
                .thenReturn(adsList);
        ResponseWrapperAds result = out.getAllAds();

        assertThat(result.getCount()).isEqualTo(adsList.size());
        assertThat(result.getResults()).isNotEmpty();
    }

    @Test
    public void createAds_ShouldReturnAdsDto() {
        Ads adsTest = adsMapper.createAdsDtoToAds(createAdsDto);
        when(userService.findUser(any(String.class)))
                .thenReturn(expectedUser);
        when(adsRepository.save(any(Ads.class)))
                .thenReturn(adsTest);
        verify(imageService, atMostOnce()).createImage(any(), any());

        AdsDto result = out.createAds(createAdsDto, null, auth);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(createAdsDto.getTitle());
        assertThat(result.getPrice()).isEqualTo(createAdsDto.getPrice());
    }

    @Test
    public void getAds_ShouldThrowNotFoundException() {
        when(adsRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> out.getAds(any(Integer.class)));
    }

    @Test
    public void getAds_ShouldReturnFullAdsDto() {
        when(adsRepository.findById(firstAds.getId()))
                .thenReturn(Optional.of(firstAds));

        FullAdsDto result = out.getAds(firstAds.getId());

        assertThat(result).isNotNull();
        assertThat(result.getPk()).isEqualTo(firstAds.getId());
        assertThat(result.getTitle()).isEqualTo(firstAds.getTitle());
        assertThat(result.getPrice()).isEqualTo(firstAds.getPrice());
        assertThat(result.getAuthorFirstName()).isEqualTo(expectedUser.getFirstName());
    }

    @Test
    public void removeAds_ShouldExecuteOnce() {
        List<Comment> comment = Collections.emptyList();
        List<Image> image = Collections.emptyList();
        when(adsRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(firstAds));
        when(commentRepository.findAllCommentByAdsId(any(Integer.class)))
                .thenReturn(comment);
        when(imageRepository.findAllImageByAdsId(any(Integer.class)))
                .thenReturn(image);
        verify(adsRepository, atMostOnce()).deleteById(any(Integer.class));

        out.removeAds(expectedUser.getId(), auth);
    }

    @Test
    public void updateAds_ShouldReturnModifiedAdsDto() {
        Ads createAds = adsMapper.createAdsDtoToAds(createAdsDto);
        when(adsRepository.findById(anyInt()))
                .thenReturn(Optional.of(firstAds));
        when(adsRepository.save(any()))
                .thenReturn(createAds);

        AdsDto result = out.updateAds(firstAds.getId(), createAdsDto, auth);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(createAdsDto.getPrice());
        assertThat(result.getTitle()).isEqualTo(createAdsDto.getTitle());
    }

    @Test
    public void getAdsMe_shouldReturnResponseWrapperAds() {
        when(adsRepository.findAllAdsByUserEmailIgnoreCase(expectedUser.getEmail()))
                .thenReturn(adsList);
        ResponseWrapperAds result = out.getAdsMe(expectedUser.getEmail());

        assertThat(result).isNotNull();
        assertThat(result.getCount()).isEqualTo(adsList.size());
        assertThat(result.getResults().contains(adsMapper.adsToAdsDto(firstAds))).isTrue();
        assertThat(result.getResults().contains(adsMapper.adsToAdsDto(secondAds))).isTrue();
    }
}