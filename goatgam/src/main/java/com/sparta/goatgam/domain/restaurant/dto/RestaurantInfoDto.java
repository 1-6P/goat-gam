package com.sparta.goatgam.domain.restaurant.dto;

import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

//얘가 응답 dto 역할을 합니다..!

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
    private String username;         //(받는거니까)
    private String restaurantTypeName; // (식당 타입 이름)


    //최종으로 받는 값
    public static RestaurantInfoDto convertDto(Restaurant r){
        return new RestaurantInfoDto(
                r.getRestaurantId(),
                r.getRestaurantName(),
                r.getRegionCode(),
                r.getIsPublic(),
                r.getRestaurantNumber(),
                r.isStatus(),
                r.getRestaurantAddress(),
                r.getUserName(),
                r.getRestaurantTypeId().getRestaurantTypeName() //fk로 연결되서 두번 꺼내야함;;
        );
    }


}
