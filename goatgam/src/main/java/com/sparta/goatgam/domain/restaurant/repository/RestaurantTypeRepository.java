package com.sparta.goatgam.domain.restaurant.repository;

import com.sparta.goatgam.domain.restaurant.entity.RestaurantType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantTypeRepository extends JpaRepository<RestaurantType, UUID> {
}
