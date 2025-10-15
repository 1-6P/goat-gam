package com.sparta.goatgam.domain.restaurant.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MenuSearchDto {
    private String menuId;
    private String menuImg;
    private String menuName;
    private String menuExplain;
    private BigDecimal menuPrice;
    private String menuStatus;
}