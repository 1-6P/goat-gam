package com.sparta.goatgam.domain.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

//조회 혹은 응답용으로 사용

@Getter
@AllArgsConstructor
public class RestaurantInfoDto {
    private UUID restaurantId;
    private String restaurantName;
    private int regionCode;
    private int isPublic;
    private String restaurantNumber;
    private boolean status;
    private String restaurantAddress;



    // 유저 dto 오면 merge 해야할거같아요
    // private UserDto user;;
    //private String username;


}
