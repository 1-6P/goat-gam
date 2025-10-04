package com.sparta.goatgam.domain.restaurant.dto;
import java.util.UUID;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RestaurantDetailDto {
    private UUID restaurantId;
    private UUID restaurantTypeId;
    private Integer restaurantTypeCode;
    private String restaurantTypeName;
    private String restaurantName;
    //private String userId;
    private Long userId; //저희 userId는 현재 Long 타입입니다! String으로 꼭 남겨야하는게 아니라면, Long을 그대로 사용해야해요
    private String userName;
    private int regionCode;
    private int isPublic;
    private String restaurantAddress;
    private String restaurantNumber;
    private boolean status;

    // 엔티티 -> DTO 변환
    //진현 수정 10/04
    //Restaurant의 값은 null일 수 없음
    public static RestaurantDetailDto from(Restaurant r) {
        return RestaurantDetailDto.builder()
                .restaurantId(r.getRestaurantId())
                .restaurantTypeId(r.getRestaurantTypeId().getRestaurantTypeId()) //해당 레스토랑 타입의 UUID 조회
                .restaurantTypeCode(r.getRestaurantTypeId().getRestaurantTypeCode())
                .restaurantTypeName(r.getRestaurantTypeId().getRestaurantTypeName())
                .restaurantName(r.getRestaurantName())
                .userId(r.getUser().getUserId())          // User도 optional=false 라 null 아님
                .userName(r.getUserName())                 // 스냅샷(비정규화)
                .regionCode(r.getRegionCode())
                .isPublic(r.getIsPublic())
                .restaurantAddress(r.getRestaurantAddress())
                .restaurantNumber(r.getRestaurantNumber())
                .status(r.isStatus())
                .build();

    }
}