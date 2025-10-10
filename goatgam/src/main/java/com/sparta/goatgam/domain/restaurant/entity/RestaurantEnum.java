package com.sparta.goatgam.domain.restaurant.entity;

public enum RestaurantEnum {
    Open("운영중"),
    Closed("휴무");

    private final String description;
    RestaurantEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
