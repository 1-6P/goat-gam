package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public ResultResponseDto addFood(UUID restaurantId, FoodRequestDto dto, User currentUser) {
        Restaurant restaurant = validateRestaurantOwner(restaurantId, currentUser);

        Food food = Food.builder()
                .foodName(dto.getName())
                .foodPrice(dto.getPrice())
                .foodImage(dto.getImage())
                .foodExplain(dto.getExplain())
                .foodStatus(FoodStatus.valueOf(dto.getStatus()))
                .restaurant(restaurant)
                .build();

        foodRepository.save(food);
        return new ResultResponseDto("success", food.getId());
    }

    @Transactional
    public ResultResponseDto updateFood(UUID restaurantId, UUID menuId, FoodRequestDto foodRequestDto, User currentUser) {
        validateRestaurantOwner(restaurantId, currentUser);
        Food food = validateFoodInRestaurant(menuId, restaurantId);

        food.update(foodRequestDto);
        return new ResultResponseDto("success", food.getId());
    }

    @Transactional
    public ResultResponseDto deleteFood(UUID restaurantId, UUID menuId, User currentUser) {
        validateRestaurantOwner(restaurantId, currentUser);
        Food food = validateFoodInRestaurant(menuId, restaurantId);

        food.changeStatus(FoodStatus.Deleted);
        food.deleted(currentUser.getNickname());

        return new ResultResponseDto("success", menuId);
    }

    //음식점 권한 조회
    public Restaurant validateRestaurantOwner(UUID restaurantId, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));

        if (!restaurant.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
        }
        return restaurant;
    }

    //음식이 해당 매장의 음식인지 확인
    public Food validateFoodInRestaurant(UUID menuId, UUID restaurantId) {
        Food food = foodRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("해당 음식이 없습니다."));

        if (!food.getRestaurant().getRestaurantId().equals(restaurantId)) {
            throw new RuntimeException("해당 식당의 음식이 아닙니다.");
        }

        if (food.getFoodStatus().equals(FoodStatus.Deleted)) {
            throw new RuntimeException("이미 삭제된 음식입니다.");
        }
        return food;
    }
}


