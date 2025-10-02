package com.sparta.goatgam.domain.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor


public class RestaurantRequestDto {
    //Schema - api 요청 body에 어떤 값이 필요한지 예시 및 설명이 나옴
    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "식당 타입 ID", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID restaurantTypeId;//restaurant fk
    //username은 get으로 받아오기때문에 필요없음\
    @Schema(description = "지역 코드", example = "11001111")
    private int regionCode;


    @Schema(description = "식당 이름", example = "진현분식")
    private String restaurantName;
    @Schema(description = "식당 주소", example = "서울시 광화문로 123-45")
    private String restaurantAddress;
    @Schema(description = "식당 전화번호", example = "02-123-4567")
    private String restaurantNumber;

    /**
    private boolean status;
    private int isPublic;
     두 값은 기본값으로 들어감.
    **/
}


