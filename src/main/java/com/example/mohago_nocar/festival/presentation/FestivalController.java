package com.example.mohago_nocar.festival.presentation;

import com.example.mohago_nocar.festival.domain.service.FestivalUseCase;
import com.example.mohago_nocar.festival.application.response.FestivalActivePeriodResponseDto;
import com.example.mohago_nocar.festival.application.response.FestivalResponseDto;
import com.example.mohago_nocar.global.common.dto.PagedResponseDto;
import com.example.mohago_nocar.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/festivals")
@RequiredArgsConstructor
@Tag(name = "Festival", description = "축제")
public class FestivalController {

    private final FestivalUseCase festivalUseCase;

    @Operation(summary = "축제 조회", description = "축제 정보를 반환합니다. 페이지네이션이 적용되어있습니다.")
    @GetMapping("")
    public ApiResponse<PagedResponseDto<FestivalResponseDto>> fetchFestivals(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDto<FestivalResponseDto> pagedResponse = festivalUseCase.fetchFestivals(pageable);
        return ApiResponse.ok(pagedResponse);
    }

    @Operation(summary = "축제 일정 조회", description = "지정된 축제의 일정을 조회합니다.")
    @GetMapping("/{festivalId}/active-period")
    public ApiResponse<FestivalActivePeriodResponseDto> findFestivalDate(@PathVariable Long festivalId) {
        FestivalActivePeriodResponseDto activePeriodResponse = festivalUseCase.getFestivalActivePeriod(festivalId);
        return ApiResponse.ok(activePeriodResponse);
    }
}
