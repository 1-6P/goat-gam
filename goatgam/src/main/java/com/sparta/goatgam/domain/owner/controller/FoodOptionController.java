package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodOptionService;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurant/{restaurantId}/menu/{menuId}/option")
public class FoodOptionController {

    private final FoodOptionService foodOptionService;

    @PostMapping
    public ResultResponseDto create(@PathVariable UUID restaurantId,
                                    @PathVariable UUID menuId,
                                    @RequestBody FoodOptionRequestDto foodOptionRequestDto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return foodOptionService.addOption(restaurantId, menuId, foodOptionRequestDto, userDetails.getUser());
    }
}
