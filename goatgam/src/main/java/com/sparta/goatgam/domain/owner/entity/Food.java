package com.sparta.goatgam.domain.owner.entity;

import com.sparta.goatgam.domain.owner.dto.FoodRequestDto;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_food")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Food extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "food_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "food_name", nullable = false, length = 50)
    private String foodName;

    @Column(name = "food_price", nullable = false)
    private int foodPrice;

    @Column(name = "food_image")
    private String foodImage;

    @Column(name = "food_explain")
    private String foodExplain;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_status", nullable = false)
    private FoodStatus foodStatus;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "restaurant_id", nullable = false)
//    private Restaurant restaurant;


    public void update(FoodRequestDto dto) {
        this.foodName = dto.getName();
        this.foodPrice = dto.getPrice();
        this.foodImage = dto.getImage();
        this.foodExplain = dto.getExplain();
        this.foodStatus = FoodStatus.valueOf(dto.getStatus());
    }

    public void changeStatus(FoodStatus status) {
        this.foodStatus = status;
    }
}