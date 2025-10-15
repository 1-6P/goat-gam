package com.sparta.goatgam.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FoodRequestDto {
    private String name;
    private BigDecimal price;
    private String image;
    private String explain;
    private String status;
}
