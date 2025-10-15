package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.ai.dto.AIRequestDto;
import com.sparta.goatgam.domain.ai.service.AIService;
import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final AIService aiService;

    @Transactional
    public ResultResponseDto addFood(UUID restaurantId, FoodRequestDto dto, boolean ai, User currentUser) {
        Restaurant restaurant = validateRestaurantOwner(restaurantId, currentUser);

        Optional<Food> existingFoodOpt = foodRepository.findByRestaurantAndFoodName(restaurant, dto.getName());

        if (existingFoodOpt.isPresent()) {
            Food existingFood = existingFoodOpt.get();

            if (existingFood.getFoodStatus() == FoodStatus.Ok || existingFood.getFoodStatus() == FoodStatus.Hidden) {
                throw new BusinessException(ExceptionCode.FOOD_DUPLICATED);
            }

            if (existingFood.getFoodStatus() == FoodStatus.Deleted) {
                existingFood.update(dto);
                return new ResultResponseDto("restored success", existingFood.getId());
            }
        }

        Food food = Food.builder()
                .foodName(dto.getName())
                .foodPrice(dto.getPrice())
                .foodImage(dto.getImage())
                .foodExplain(dto.getExplain())
                .foodStatus(FoodStatus.valueOf(dto.getStatus()))
                .restaurant(restaurant)
                .build();

        foodRepository.save(food);

        if (ai) {
            AIRequestDto aiRequestDto = new AIRequestDto(dto.getExplain());
            food.updateExplain(aiService.createAiRequest(food.getId(), currentUser, aiRequestDto).getMessage());
        }

        return new ResultResponseDto("create success", food.getId());
    }

    @Transactional
    public ResultResponseDto updateFood(UUID restaurantId, UUID menuId, FoodRequestDto foodRequestDto, User currentUser) {
        validateRestaurantOwner(restaurantId, currentUser);
        Food food = validateFoodInRestaurant(menuId, restaurantId);

        food.update(foodRequestDto);
        return new ResultResponseDto("update success", food.getId());
    }

    @Transactional
    public ResultResponseDto deleteFood(UUID restaurantId, UUID menuId, User currentUser) {
        validateRestaurantOwner(restaurantId, currentUser);
        Food food = validateFoodInRestaurant(menuId, restaurantId);

        food.changeStatus(FoodStatus.Deleted);
        food.deleted(currentUser.getNickname());

        return new ResultResponseDto("delete success", menuId);
    }

    //음식점 권한 조회
    public Restaurant validateRestaurantOwner(UUID restaurantId, User currentUser) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.RESTAURANT_NOT_FOUND));

        if (!restaurant.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_RESTAURANT);
        }
        return restaurant;
    }

    //음식이 해당 매장의 음식인지 확인
    public Food validateFoodInRestaurant(UUID menuId, UUID restaurantId) {
        Food food = foodRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(ExceptionCode.FOOD_NOT_FOUND));

        if (!food.getRestaurant().getRestaurantId().equals(restaurantId)) {
            throw new BusinessException(ExceptionCode.FOOD_INPUT_ERROR);
        }

        if (food.getFoodStatus().equals(FoodStatus.Deleted)) {
            throw new BusinessException(ExceptionCode.FOOD_ALREADY_DELETED);
        }
        return food;
    }
}


