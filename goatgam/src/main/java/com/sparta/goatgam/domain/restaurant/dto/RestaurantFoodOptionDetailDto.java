package com.sparta.goatgam.domain.restaurant.dto;

import com.sparta.goatgam.domain.owner.entity.FoodOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;
import java.util.UUID;


@Getter
@AllArgsConstructor
public class RestaurantFoodOptionDetailDto {
    private UUID restaurantId;
    private UUID foodId;
    private UUID optionId;
    private String optionContents;
    private int optionSurcharge;

    public static List<RestaurantFoodOptionDetailDto> convertList(UUID restaurantId, UUID foodId ,List<FoodOption> foodOption) {
        return foodOption.stream().
                map(fo -> new RestaurantFoodOptionDetailDto(
                        restaurantId,
                        foodId,
                        fo.getId(),
                        fo.getContents(),
                        fo.getSurcharge()
                )).toList();
    }
}
