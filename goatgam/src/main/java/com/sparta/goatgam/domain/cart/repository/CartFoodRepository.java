package com.sparta.goatgam.domain.cart.repository;

import com.sparta.goatgam.domain.cart.entity.Cart;
import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.owner.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartFoodRepository extends JpaRepository<CartFood, UUID> {

    List<CartFood> findAllByCartAndFoodAndIsDeletedFalse(Cart cart, Food food);
}
