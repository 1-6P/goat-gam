package com.sparta.goatgam.domain.cart.entity;

import com.sparta.goatgam.domain.owner.entity.FoodOption;
import com.sparta.goatgam.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_cart_food_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartFoodOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_food_option_id", nullable = false, updatable = false)
    private UUID cartFoodOptionId;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_option_id")
    private FoodOption foodOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_food_id")
    private CartFood cartFood;
}
