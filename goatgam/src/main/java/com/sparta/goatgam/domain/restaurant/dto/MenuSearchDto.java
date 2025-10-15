package com.sparta.goatgam.domain.restaurant.dto;

import java.awt.*;

import com.sparta.goatgam.domain.owner.entity.Food;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuSearchDto {
	private String menuId;
	private String menuImg;
	private String menuName;
	private String menuExplain;
	private int menuPrice;
	private String menuStatus;

	public static MenuSearchDto from(Food food) {
		return MenuSearchDto.builder()
							.menuId(food.getId().toString())
							.menuImg(food.getFoodImage())
							.menuName(food.getFoodName())
							.menuExplain(food.getFoodExplain())
							.menuPrice(food.getFoodPrice())
							.menuStatus(food.getFoodStatus().name())
							.build();
	}
}