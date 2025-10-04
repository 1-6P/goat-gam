package com.sparta.goatgam.domain.owner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FoodOptionRequestDto {
    private String contents;
    private int surcharge;
}
