package com.sparta.goatgam.domain.restaurant.dto;

import com.sparta.goatgam.domain.restaurant.entity.RestaurantEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantUpdateDto {
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantNumber;
    private int regionCode; //null 허용
    private RestaurantEnum isPublic;
}
