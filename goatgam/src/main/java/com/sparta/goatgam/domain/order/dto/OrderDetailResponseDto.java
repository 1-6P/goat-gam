package com.sparta.goatgam.domain.order.dto;

import com.sparta.goatgam.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderDetailResponseDto {
    private UUID orderId;
    private String restaurantName;
    private String address;
    private List<OrderFoodItemDto> foods;
    private BigDecimal totalPrice;
    private LocalDateTime orderTime;

    public OrderDetailResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.restaurantName = order.getRestaurant().getRestaurantName();
        this.address = order.getAddress();
        this.foods = order.getOrderFoods().stream().map(OrderFoodItemDto::new).toList();
        this.totalPrice = order.getTotalPrice();
        this.orderTime = order.getOrderTime();
    }
}