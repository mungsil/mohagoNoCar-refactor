package com.example.mohago_nocar.festival.domain.service;

import com.example.mohago_nocar.festival.domain.model.Festival;
import com.example.mohago_nocar.festival.application.response.FestivalActivePeriodResponseDto;
import com.example.mohago_nocar.festival.application.response.FestivalLocationResponseDto;
import com.example.mohago_nocar.festival.application.response.FestivalResponseDto;
import com.example.mohago_nocar.global.common.dto.PagedResponseDto;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface FestivalUseCase {

    PagedResponseDto<FestivalResponseDto> fetchFestivals(Pageable pageable);

    FestivalLocationResponseDto getFestivalLocation(Long festivalId);

    FestivalActivePeriodResponseDto getFestivalActivePeriod(Long festivalId);

    List<Festival> getAllFestivals();

    Festival getFestival(Long festivalId);
}
