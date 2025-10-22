package com.example.mohago_nocar.festival.application;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.domain.model.FestivalImage;
import com.example.mohago_nocar.festival.domain.repository.FestivalRepository;
import com.example.mohago_nocar.festival.domain.service.FestivalImageUseCase;
import com.example.mohago_nocar.festival.domain.service.FestivalUseCase;
import com.example.mohago_nocar.festival.application.response.FestivalActivePeriodResponseDto;
import com.example.mohago_nocar.festival.application.response.FestivalLocationResponseDto;
import com.example.mohago_nocar.festival.application.response.FestivalResponseDto;
import com.example.mohago_nocar.global.common.dto.PagedResponseDto;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FestivalService implements FestivalUseCase {

    private final FestivalRepository festivalRepository;
    private final FestivalImageUseCase festivalImageUseCase;

    @Override
    public PagedResponseDto<FestivalResponseDto> fetchFestivals(Pageable pageable) {
        Page<Festival> pagedFestival = festivalRepository.getFestivals(pageable);
        Page<FestivalResponseDto> pagedFestivalResponseDto =
                pagedFestival.map(festival -> {
                            List<FestivalImage> festivalImages = festivalImageUseCase.getAllFestivalImages(festival.getId());
                            List<String> festivalImagesUrl = festivalImages.stream().map(FestivalImage::getImageUrl).toList();
                            return FestivalResponseDto.of(festival, festivalImagesUrl);
                        }
                );
        return new PagedResponseDto<>(pagedFestivalResponseDto);
    }

    @Override
    public FestivalLocationResponseDto getFestivalLocation(Long festivalId) {
        Festival festival = festivalRepository.getFestivalById(festivalId);

        return FestivalLocationResponseDto.of(festival.getCoordinate());
    }

    @Override
    public FestivalActivePeriodResponseDto getFestivalActivePeriod(Long festivalId) {
        Festival festival = festivalRepository.getFestivalById(festivalId);
        return FestivalActivePeriodResponseDto.of(festival.getActivePeriod());
    }

    @Override
    public List<Festival> getAllFestivals() {
        return festivalRepository.getAllFestivals();
    }

    @Override
    public Festival getFestival(Long festivalId) {
        return festivalRepository.getFestivalById(festivalId);
    }
}
