package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant/{restaurantId}/menu")
@Tag(name = "음식 API", description = "음식 관련 기능 API입니다.")
public class FoodController {
    private final FoodService foodService;

    @Operation(summary = "음식 등록", description = "새로운 음식을 등록")
    @PostMapping
    public ResultResponseDto addFood(@PathVariable UUID restaurantId,
                                     @RequestBody FoodRequestDto foodRequestDto,
                                     @RequestParam(defaultValue = "false") boolean ai,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return foodService.addFood(restaurantId, foodRequestDto, ai, userDetails.getUser());
    }

    @Operation(summary = "음식 수정", description = "등록된 음식의 정보를 수정")
    @PutMapping("/{menuId}")
    public ResultResponseDto updateFood(@PathVariable UUID restaurantId,
                                        @PathVariable UUID menuId,
                                        @RequestBody FoodRequestDto foodRequestDto,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return foodService.updateFood(restaurantId, menuId, foodRequestDto, userDetails.getUser());
    }

    @Operation(summary = "음식 삭제", description = "특정 음식의 정보를 삭제")
    @DeleteMapping("/{menuId}")
    public ResultResponseDto deleteFood(@PathVariable UUID restaurantId,
                                        @PathVariable UUID menuId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return foodService.deleteFood(restaurantId, menuId, userDetails.getUser());
    }
}
