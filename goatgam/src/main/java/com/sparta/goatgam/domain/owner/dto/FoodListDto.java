package com.sparta.goatgam.domain.owner.dto;

import com.sparta.goatgam.domain.owner.entity.Food;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FoodListDto {
    private UUID foodId;
    private String name;
    private Integer price;
    private String image;
    private String explain;
    private String status; // Ok/Soldout/Hidden/Deleted

    public static FoodListDto from(Food f) {
        return new FoodListDto(
                f.getId(),
                f.getFoodName(),
                f.getFoodPrice(),
                f.getFoodImage(),
                f.getFoodExplain(),
                f.getFoodStatus() == null ? null : f.getFoodStatus().name()
        );
    }
}
