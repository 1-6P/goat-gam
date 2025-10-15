package com.sparta.goatgam.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FoodOptionRequestDto {
    private String contents;
    private BigDecimal surcharge;
}
