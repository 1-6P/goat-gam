package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant/{restaurantId}/menu/{menuId}/option")
public class FoodOptionController {

    private final FoodOptionService foodOptionService;

    @PostMapping
    public ResultResponseDto create(@PathVariable UUID restaurantId, @PathVariable UUID menuId, @RequestBody FoodOptionRequestDto foodOptionRequestDto) {
        return foodOptionService.addOption(restaurantId, menuId, foodOptionRequestDto);
    }
}
