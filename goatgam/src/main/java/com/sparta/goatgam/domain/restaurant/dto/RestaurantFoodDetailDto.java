package com.sparta.goatgam.domain.restaurant.dto;

import com.sparta.goatgam.domain.owner.entity.Food;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RestaurantFoodDetailDto {
    private UUID foodId;
    private String foodName;
    private String foodExplain;
    private int foodPrice;
    private String foodStatus;
    private String restaurantName;

    public static RestaurantFoodDetailDto convertDto(Food food) {
        return new RestaurantFoodDetailDto(
                food.getId(),
                food.getFoodName(),
                food.getFoodExplain(),
                food.getFoodPrice(),
                food.getFoodStatus().toString(),
                food.getRestaurant().getRestaurantName()
        );
    }

}
