package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodOptionService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant/{restaurantId}/menu/{menuId}/option")
@Tag(name = "음식 옵션 API", description = "음식 옵션 관련 기능 API입니다.")
public class FoodOptionController {

    private final FoodOptionService foodOptionService;

    @Operation(summary = "음식 옵션 등록", description = "새로운 음식의 옵션을 등록")
    @PostMapping
    public ResultResponseDto createOption(@PathVariable UUID restaurantId,
                                          @PathVariable UUID menuId,
                                          @RequestBody FoodOptionRequestDto foodOptionRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return foodOptionService.addOption(restaurantId, menuId, foodOptionRequestDto, userDetails.getUser());
    }

    @Operation(summary = "음식 옵션 수정", description = "등록된 음식의 옵션을 수정")
    @PutMapping("/{optionId}")
    public ResultResponseDto updateOption(@PathVariable UUID restaurantId,
                                          @PathVariable UUID menuId,
                                          @PathVariable UUID optionId,
                                          @RequestBody FoodOptionRequestDto foodOptionRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return foodOptionService.updateOption(restaurantId, menuId, optionId, foodOptionRequestDto, userDetails.getUser());
    }

    @Operation(summary = "음식 옵션 삭제", description = "특정 음식의 옵션을 삭제")
    @DeleteMapping("/{optionId}")
    public ResultResponseDto deleteFood(@PathVariable UUID restaurantId,
                                        @PathVariable UUID menuId,
                                        @PathVariable UUID optionId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return foodOptionService.deleteOption(restaurantId, menuId, optionId, userDetails.getUser());
    }
}
