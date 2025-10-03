package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
import com.sparta.goatgam.domain.owner.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodOptionService {
    private final FoodOptionRepository foodOptionRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;

    @Transactional
    public ResultResponseDto addOption(UUID restaurantId, UUID menuId, FoodOptionRequestDto foodOptionRequestDto) {
        //        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 없습니다."));
//
//        if (!restaurant.getOwner().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("해당 식당에 대한 권한이 없습니다.");
//        }

        Food food = foodRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("해당 음식이 없습니다."));

        if(food.getFoodStatus() == FoodStatus.Deleted){
            throw new RuntimeException("옵션을 추가할 수 있는 상태가 아닙니다.");
        }

//        if (!food.getRestaurant().getId().equals(restaurantId)) {
//            throw new RuntimeException("해당 식당의 음식이 아닙니다.");
//        }

        FoodOption foodOption = FoodOption.builder()
                .contents(foodOptionRequestDto.getContents())
                .surcharge(foodOptionRequestDto.getSurcharge())
                .food(food)
                .build();

        foodOptionRepository.save(foodOption);

        return new ResultResponseDto("success", foodOption.getId());
    }


}
