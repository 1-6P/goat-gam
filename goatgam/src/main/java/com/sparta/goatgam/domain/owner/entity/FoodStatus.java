package com.sparta.goatgam.domain.owner.entity;

public enum FoodStatus {
    Ok("판매중"),
    Soldout("품절"),
    Hidden("숨김"),
    Deleted("삭제됨");

    private final String description;

    FoodStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

