package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.FoodResponseDto;
import com.sparta.goatgam.domain.owner.service.FoodService;
import com.sparta.goatgam.global.security.security.UserDetailsImpl;
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
    public FoodResponseDto addFood(
            @PathVariable UUID restaurantId,
            @RequestBody FoodRequestDto foodRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails) {

//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
//        User currentUser = userDetails.getUser();
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }

        return foodService.addFood(restaurantId, foodRequestDto);
    }
}
