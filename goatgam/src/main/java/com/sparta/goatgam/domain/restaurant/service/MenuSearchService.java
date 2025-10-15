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
			throw new IllegalArgumentException("식당을 찾을 수 없습니다: " + restaurantId);
		}

		// 키워드 공백 검사
		if (keyword == null || keyword.isBlank()) {
			return List.of();
		}

		// 이름/설명 검색 후 중복 제거
		Set<Food> foods = new HashSet<>();
		foods.addAll(menuRepository.findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(restaurantId, keyword));
		foods.addAll(menuRepository.findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(restaurantId, keyword));

		// Hidden, Deleted 제외 후 변환
		return foods.stream()
					.filter(f -> f.getFoodStatus() != FoodStatus.Hidden && f.getFoodStatus() != FoodStatus.Deleted)
					.map(FoodListDto::from)
					.collect(Collectors.toList());
	}
}
