package com.sparta.goatgam.domain.owner.repository;

import com.sparta.goatgam.domain.owner.entity.FoodOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FoodOptionRepository extends JpaRepository<FoodOption, UUID> {
}
