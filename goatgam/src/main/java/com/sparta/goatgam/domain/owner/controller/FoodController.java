package com.sparta.goatgam.domain.owner.controller;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.FoodResponseDto;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.owner.service.FoodService;
import com.sparta.goatgam.domain.user.entity.User;
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
    private final FoodRepository foodRepository;

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

    @PostMapping("/{menuId}")
    public FoodResponseDto updateFood(@PathVariable UUID restaurantId,
                                      @PathVariable UUID menuId,
                                      @RequestBody FoodRequestDto foodRequestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
//        User currentUser = userDetails.getUser();
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }


        return foodService.updateFood(restaurantId, menuId, foodRequestDto);
    }

    @DeleteMapping("/{menuId}")
    public FoodResponseDto deleteFood(@PathVariable UUID restaurantId,
                                      @PathVariable UUID menuId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
        User currentUser = userDetails.getUser();
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }

        return foodService.deleteFood(restaurantId, menuId, currentUser.getNickname());
    }
}
