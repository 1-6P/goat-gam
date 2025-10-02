package com.sparta.goatgam.domain.restaurant.entity;

import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_restaurant_code")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // ✅ 기본 생성자 JPA용
public class RestaurantType extends BaseEntity {

    @Id
    @Column(name = "restaurant_type_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID restaurantTypeId;

    @Column(name = "restaurant_type_code", nullable = false)
    private Integer restaurantTypeCode;

    @Column(name = "restaurant_type_name")
    private String restaurantTypeName;

     //GPT야 고마워
    public static RestaurantType create(
            Integer restaurantTypeCode,
            String restaurantTypeName
    ) {
        return RestaurantType.builder()
                .restaurantTypeCode(restaurantTypeCode)
                .restaurantTypeName(restaurantTypeName).build();
    }

}
