package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodOptionService {
    private final FoodOptionRepository foodOptionRepository;
    private final FoodService foodService;

    @Transactional
    public ResultResponseDto addOption(UUID restaurantId, UUID menuId, FoodOptionRequestDto foodOptionRequestDto, User currentUser) {
        foodService.validateRestaurantOwner(restaurantId, currentUser);
        Food food = foodService.validateFoodInRestaurant(menuId, restaurantId);

        FoodOption foodOption = FoodOption.builder()
                .contents(foodOptionRequestDto.getContents())
                .surcharge(foodOptionRequestDto.getSurcharge())
                .food(food)
                .build();

        foodOptionRepository.save(foodOption);

        return new ResultResponseDto("success", foodOption.getId());
    }

    @Transactional
    public ResultResponseDto updateOption(UUID restaurantId, UUID menuId, UUID optionId, FoodOptionRequestDto foodOptionRequestDto, User user) {
        foodService.validateRestaurantOwner(restaurantId, user);
        Food food = foodService.validateFoodInRestaurant(menuId, restaurantId);
        FoodOption foodOption = foodOptionRepository.findById(optionId).orElseThrow(() -> new RuntimeException("해당 옵션이 없습니다."));

        foodOption.update(foodOptionRequestDto);

        return new ResultResponseDto("success", foodOption.getId());
    }
}
