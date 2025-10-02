package com.sparta.goatgam.domain.restaurant.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter


public class RestaurantRequestDto {
    private Long userId;
    private String userName;
    private int regionCode;
    private int isPublic;
    private UUID restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantNumber;
    private boolean status;

}
