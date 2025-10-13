package com.sparta.goatgam.domain.cart.dto;

import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CartFoodResponseDto {
    private UUID cartFoodId;
    private UUID foodId;
    private String foodName;
    private String foodImage;
    private int price;
    private int quantity;
    private FoodStatus foodStatus;
    private List<CartFoodOptionResponseDto> cartFoodOptions;

    public CartFoodResponseDto(CartFood cartFood) {
        this.cartFoodId = cartFood.getCartFoodId();
        this.foodId = cartFood.getFood().getId();
        this.foodName = cartFood.getFood().getFoodName();
        this.foodImage = cartFood.getFood().getFoodImage();
        this.price = cartFood.getFood().getFoodPrice();
        this.quantity = cartFood.getQuantity();
        this.foodStatus = cartFood.getFood().getFoodStatus();
        this.cartFoodOptions = cartFood.getCartFoodOptions().stream().map(CartFoodOptionResponseDto::new).toList();
    }
}
