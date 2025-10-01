package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.domain.owner.dto.FoodResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.entity.Restaurant;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import com.sparta.goatgam.domain.owner.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public FoodResponseDto addFood(UUID restaurantId, FoodRequestDto foodRequestDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        Food food = new Food(foodRequestDto.getName(),
                foodRequestDto.getPrice(),
                foodRequestDto.getImage(),
                foodRequestDto.getExplain(),
                FoodStatus.valueOf(foodRequestDto.getStatus()),
                restaurant);

        Long id = foodRepository.save(food).getId();

        return new FoodResponseDto("success", id);
    }
}

