package com.sparta.goatgam.domain.restaurant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.goatgam.domain.owner.entity.Food;

public interface MenuRepository extends JpaRepository<Food, UUID> {
	// 음식명 검색
	List<Food> findByRestaurant_RestaurantIdAndFoodNameContainingIgnoreCase(UUID restaurantId, String keyword);

	// 음식 설명 검색
	List<Food> findByRestaurant_RestaurantIdAndFoodExplainContainingIgnoreCase(UUID restaurantId, String keyword);
}
