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
    private String userId;
    private String userName;
    private int regionCode;
    private int isPublic;
    private String restaurantAddress;
    private String restaurantNumber;
    private boolean status;

    // 엔티티 -> DTO 변환
    public static RestaurantDetailDto from(Restaurant r) {
        RestaurantType t = r.getRestaurantTypeId();
        return RestaurantDetailDto.builder()
                .restaurantId(r.getRestaurantId())
                .restaurantTypeId(t != null ? t.getRestaurantTypeId() : null)
                .restaurantTypeCode(t != null ? t.getRestaurantTypeCode() : null)
                .restaurantTypeName(t != null ? t.getRestaurantTypeName() : null)
                .restaurantName(r.getRestaurantName())
                .userId(r.getUser() != null && r.getUser().getUserId() != null
                        ? r.getUser().getUserId().toString() : null)
                .userName(r.getUserName())
                .regionCode(r.getRegionCode())
                .isPublic(r.getIsPublic())
                .restaurantAddress(r.getRestaurantAddress())            .restaurantNumber(r.getRestaurantNumber())
                .status(r.isStatus())
                .build();
    }
}