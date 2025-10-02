package com.sparta.goatgam.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FoodRequestDto {
    private String name;
    private int price;
    private String image;
    private String explain;
    private String status;
}
