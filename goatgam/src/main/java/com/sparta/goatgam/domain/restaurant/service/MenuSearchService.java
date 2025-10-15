package com.sparta.goatgam.domain.restaurant.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.goatgam.domain.owner.dto.FoodListDto;
import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodStatus;
import com.sparta.goatgam.domain.restaurant.repository.MenuRepository;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuSearchService {

	private final MenuRepository menuRepository;
	private final RestaurantRepository restaurantRepository;

	public List<FoodListDto> searchMenus(UUID restaurantId, String keyword) {
		// 식당 존재 여부 확인
		if (!restaurantRepository.existsById(restaurantId)) {
			throw new BusinessException(ExceptionCode.RESTAURANT_NOT_FOUND);
		}

		// 키워드 공백 검사
		if (keyword == null || keyword.isBlank()) {
			throw new BusinessException(ExceptionCode.INVALID_INPUT);
		}

		// 이름/설명 검색 후 중복 제거
		Set<Food> foods = new HashSet<>();
		foods.addAll(menuRepository.findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(restaurantId, keyword));
		foods.addAll(menuRepository.findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(restaurantId, keyword));

		// 검색 결과가 없을 경우
		if (foods.isEmpty()) {
			throw new BusinessException(ExceptionCode.FOOD_NOT_FOUND);
		}

		// Hidden, Deleted 제외 후 변환
		return foods.stream()
					.filter(f -> f.getFoodStatus() != FoodStatus.Hidden && f.getFoodStatus() != FoodStatus.Deleted)
					.map(FoodListDto::from)
					.collect(Collectors.toList());
	}
}
