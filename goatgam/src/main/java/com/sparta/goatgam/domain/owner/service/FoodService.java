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
                .foodStatus(FoodStatus.valueOf(foodRequestDto.getStatus())).build();

        UUID id = foodRepository.save(food).getId();

        return new FoodResponseDto("success", id);
    }
}

