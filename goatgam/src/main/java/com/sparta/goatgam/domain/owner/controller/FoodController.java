package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant/{restaurantId}/menu")
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public ResultResponseDto addFood(@PathVariable UUID restaurantId,
                                     @RequestBody FoodRequestDto foodRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return foodService.addFood(restaurantId, foodRequestDto, userDetails.getUser());
    }

    @PutMapping("/{menuId}")
    public ResultResponseDto updateFood(@PathVariable UUID restaurantId,
                                      @PathVariable UUID menuId,
                                      @RequestBody FoodRequestDto foodRequestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return foodService.updateFood(restaurantId, menuId, foodRequestDto, userDetails.getUser());
    }

    @DeleteMapping("/{menuId}")
    public ResultResponseDto deleteFood(@PathVariable UUID restaurantId,
                                      @PathVariable UUID menuId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return foodService.deleteFood(restaurantId, menuId, userDetails.getUser());
    }
}
