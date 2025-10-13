package com.sparta.goatgam.domain.cart.dto;

import com.sparta.goatgam.domain.cart.entity.Cart;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CartResponseDto {
    private UUID cartId;
    private CartRestaurantResponseDto restaurant;
    private List<CartFoodResponseDto> cartFoods;

    public CartResponseDto(Cart cart) {
        this.cartId = cart.getCartId();
        this.restaurant = new CartRestaurantResponseDto(cart.getRestaurant());
        this.cartFoods = cart.getCartFoods().stream().map(CartFoodResponseDto::new).toList();
    }
}
