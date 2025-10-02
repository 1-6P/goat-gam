package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.FoodResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant/{restaurantId}/menu")
public class FoodController {
    private final FoodService foodService;

//    @PreAuthorize("Owner")
    @PostMapping
    public FoodResponseDto addFood(
            @PathVariable UUID restaurantId,
            @RequestBody FoodRequestDto foodRequestDto) {
         return foodService.addFood(restaurantId, foodRequestDto);
    }
}
