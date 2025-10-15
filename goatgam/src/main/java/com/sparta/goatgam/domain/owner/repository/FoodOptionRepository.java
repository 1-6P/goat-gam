package com.sparta.goatgam.domain.owner.repository;

import com.sparta.goatgam.domain.owner.entity.Food;
import com.sparta.goatgam.domain.owner.entity.FoodOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FoodOptionRepository extends JpaRepository<FoodOption, UUID> {
    List<FoodOption> findByFood_IdAndDeletedFalse(UUID foodId);

    Optional<FoodOption> findByFoodAndContents(Food food, String contents);
}
