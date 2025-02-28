package com.example.mohago_nocar.place.presentation;

import com.example.mohago_nocar.global.common.response.ApiResponse;
import com.example.mohago_nocar.place.domain.service.PlaceUseCase;
import com.example.mohago_nocar.place.presentation.response.NearPlaceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/festivals")
@RequiredArgsConstructor
@Tag(name = "Place", description = "축제 주변 장소")
public class PlaceController {

    private final PlaceUseCase placeUseCase;

    @Operation(summary = "축제 주변 장소 조회하기", description = "축제 주변 장소 정보를 조회합니다. ")
    @GetMapping("/{festivalId}/nearby-places")
    public ApiResponse<List<NearPlaceResponseDto>> getFestivalNearPlaces(
            @PathVariable(name = "festivalId") Long festivalId
    ) {
        List<NearPlaceResponseDto> places = placeUseCase.getFestivalNearPlaces(festivalId);
        return ApiResponse.ok(places);
    }

/*
    @Operation(summary = "축제 주변 장소 조회하기", description = "축제 주변 장소 정보를 조회합니다. ")
    @GetMapping("/{festivalId}")
    public ApiResponse<PagedResponseDto<NearPlaceResponseDto>> getFestivalNearPlaces(
            @PathVariable(name = "festivalId") Long festivalId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDto<NearPlaceResponseDto> places = placeUseCase.getFestivalNearPlaces(festivalId, pageable);
        return ApiResponse.ok(places);
    }*/

}
