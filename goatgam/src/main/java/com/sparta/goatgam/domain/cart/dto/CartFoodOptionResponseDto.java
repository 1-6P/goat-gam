package com.sparta.goatgam.domain.cart.dto;

import com.sparta.goatgam.domain.cart.entity.CartFoodOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CartFoodOptionResponseDto {
    private UUID cartFoodOptionId;
    private UUID foodOptionId;
    private String content;
    private BigDecimal price;

    public CartFoodOptionResponseDto(CartFoodOption cartFoodOption) {
        this.cartFoodOptionId = cartFoodOption.getCartFoodOptionId();
        this.foodOptionId = cartFoodOption.getFoodOption().getId();
        this.content = cartFoodOption.getFoodOption().getContents();
        this.price = cartFoodOption.getFoodOption().getSurcharge();
    }
}
