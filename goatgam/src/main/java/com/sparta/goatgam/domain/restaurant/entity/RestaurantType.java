package com.sparta.goatgam.domain.restaurant.entity;

import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_restaurant_code")
public class RestaurantType extends BaseEntity {

    @Id
    @Column(name = "restaurant_type_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID restaurantTypeId;

    @Column(name = "restaurant_type_code", nullable = false)
    private Integer restaurantTypeCode;

    @Column(name = "restaurant_type_name")
    private String restaurantTypeName;

}
