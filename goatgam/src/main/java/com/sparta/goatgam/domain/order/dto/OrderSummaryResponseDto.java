package com.sparta.goatgam.domain.order.dto;

import com.sparta.goatgam.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderSummaryResponseDto {
    private UUID orderId;
    private String restaurantName;
    private String address;
    private LocalDateTime orderTime;
    private int totalPrice;

    public OrderSummaryResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.restaurantName = order.getRestaurant().getRestaurantName();
        this.address = order.getAddress();
        this.orderTime = order.getOrderTime();
        this.totalPrice = order.getTotalPrice();
    }
}
