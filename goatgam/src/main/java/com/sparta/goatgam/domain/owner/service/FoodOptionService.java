package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.owner.dto.FoodOptionRequestDto;
import com.sparta.goatgam.domain.owner.dto.ResultResponseDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.domain.owner.repository.FoodOptionRepository;
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
public class FoodOptionService {
    private final FoodOptionRepository foodOptionRepository;
    private final FoodService foodService;

    @Transactional
    public ResultResponseDto addOption(UUID restaurantId, UUID menuId, FoodOptionRequestDto foodOptionRequestDto, User currentUser) {
        foodService.validateRestaurantOwner(restaurantId, currentUser);
        Food food = foodService.validateFoodInRestaurant(menuId, restaurantId);

        Optional<FoodOption> existingOptionOpt = foodOptionRepository.findByFoodAndContents(food, foodOptionRequestDto.getContents());

        if (existingOptionOpt.isPresent()) {
            FoodOption existingOption = existingOptionOpt.get();

            if (!existingOption.isDeleted()) {
                throw new BusinessException(ExceptionCode.OPTION_DUPLICATED);
            }else{
                existingOption.changeStatus(false);
                existingOption.update(foodOptionRequestDto);
                return new ResultResponseDto("restored success", existingOption.getId());
            }
        }

        FoodOption foodOption = FoodOption.builder()
                .contents(foodOptionRequestDto.getContents())
                .surcharge(foodOptionRequestDto.getSurcharge())
                .food(food)
                .build();

        foodOptionRepository.save(foodOption);

        return new ResultResponseDto("create success", foodOption.getId());
    }

    @Transactional
    public ResultResponseDto updateOption(UUID restaurantId, UUID menuId, UUID optionId, FoodOptionRequestDto foodOptionRequestDto, User user) {
        foodService.validateRestaurantOwner(restaurantId, user);
        foodService.validateFoodInRestaurant(menuId, restaurantId);
        FoodOption foodOption = foodOptionRepository.findById(optionId).orElseThrow(() -> new BusinessException(ExceptionCode.OPTION_NOT_FOUND));

        foodOption.update(foodOptionRequestDto);

        return new ResultResponseDto("update success", foodOption.getId());
    }

    @Transactional
    public ResultResponseDto deleteOption(UUID restaurantId, UUID menuId, UUID optionId, User user) {
        foodService.validateRestaurantOwner(restaurantId, user);
        foodService.validateFoodInRestaurant(menuId, restaurantId);
        FoodOption foodOption = foodOptionRepository.findById(optionId).orElseThrow(() -> new BusinessException(ExceptionCode.OPTION_NOT_FOUND));

        if(foodOption.isDeleted()) {
            throw new BusinessException(ExceptionCode.OPTION_ALREADY_DELETED);
        }

        foodOption.changeStatus(true);
        foodOption.deleted(user.getNickname());
        return new ResultResponseDto("delete success", foodOption.getId());
    }
}
