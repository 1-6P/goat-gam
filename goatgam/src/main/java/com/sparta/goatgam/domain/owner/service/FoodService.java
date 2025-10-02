package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.FoodResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
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
    public FoodResponseDto addFood(UUID restaurantId, FoodRequestDto foodRequestDto) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Food food = Food.builder().foodName(foodRequestDto.getName())
                .foodPrice(foodRequestDto.getPrice())
                .foodImage(foodRequestDto.getImage())
                .foodExplain(foodRequestDto.getExplain())
                .foodStatus(FoodStatus.valueOf(foodRequestDto.getStatus()))
//                .restaurant(restaurant)
                .build();

        UUID id = foodRepository.save(food).getId();

        return new FoodResponseDto("success", id);
    }

    @Transactional
    public FoodResponseDto updateFood(UUID restaurantId, UUID menuId, FoodRequestDto foodRequestDto) {
        Food food = foodRepository.findById(menuId).orElseThrow(() -> new RuntimeException("해당 음식이 없습니다."));

//        if (!food.getRestaurant().getId().equals(restaurantId)) {
//            throw new RuntimeException("이 메뉴는 해당 식당의 메뉴가 아닙니다.");
//        }

        food.update(foodRequestDto);

        return new FoodResponseDto("success", food.getId());
    }

    @Transactional
    public FoodResponseDto deleteFood(UUID restaurantId, UUID menuId, String userNickname) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        Food food = foodRepository.findById(menuId).orElseThrow(() -> new RuntimeException("해당 음식이 없습니다."));

//        if (!food.getRestaurant().getId().equals(restaurantId)) {
//            throw new RuntimeException("이 메뉴는 해당 식당의 메뉴가 아닙니다.");
//        }

        food.changeStatus(FoodStatus.Deleted);
        food.deleted(userNickname);

        return new FoodResponseDto("success", menuId);
    }
}

