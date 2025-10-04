package com.sparta.goatgam.domain.owner.repository;

import com.sparta.goatgam.domain.owner.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FoodRepository extends JpaRepository<Food, UUID> {
    Optional<Food> findByIdAndRestaurant_RestaurantId(UUID foodId, UUID restaurantId);
}
