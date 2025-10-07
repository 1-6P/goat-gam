package com.sparta.goatgam.domain.order.dto;

import com.sparta.goatgam.domain.order.entity.OrderFood;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderFoodItemDto {
    private String foodName;
    private List<String> options;
    private int price;
    private int count;

    public OrderFoodItemDto(OrderFood orderFood) {
        this.foodName = orderFood.getFoodName();
        this.options = orderFood.getOptionList();
        this.price = orderFood.getPrice();
        this.count = orderFood.getCount();
    }
}