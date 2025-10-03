package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
//    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ResultResponseDto addFood(UUID restaurantId, FoodRequestDto dto, User currentUser) {

//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }

        Food food = Food.builder()
                .foodName(dto.getName())
                .foodPrice(dto.getPrice())
                .foodImage(dto.getImage())
                .foodExplain(dto.getExplain())
                .foodStatus(FoodStatus.valueOf(dto.getStatus()))
//                .restaurant(restaurant)
                .build();

        foodRepository.save(food);
        return new ResultResponseDto("success", food.getId());
    }

    @Transactional
    public ResultResponseDto updateFood(UUID restaurantId, UUID menuId, FoodRequestDto foodRequestDto, User currentUser) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }

        Food food = foodRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("해당 음식이 없습니다."));

//        if (!food.getRestaurant().getId().equals(restaurantId)) {
//            throw new RuntimeException("해당 식당의 음식이 아닙니다.");
//        }

        food.update(foodRequestDto);
        return new ResultResponseDto("success", food.getId());
    }

    @Transactional
    public ResultResponseDto deleteFood(UUID restaurantId, UUID menuId, User currentUser) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }

        Food food = foodRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("해당 음식이 없습니다."));

//        if (!food.getRestaurant().getId().equals(restaurantId)) {
//            throw new RuntimeException("해당 식당의 음식이 아닙니다.");
//        }

        food.changeStatus(FoodStatus.Deleted);
        food.deleted(currentUser.getNickname());

        return new ResultResponseDto("success", menuId);
    }
}

