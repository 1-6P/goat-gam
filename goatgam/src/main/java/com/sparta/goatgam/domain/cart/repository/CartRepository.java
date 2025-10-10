package com.sparta.goatgam.domain.cart.repository;

import com.sparta.goatgam.domain.cart.entity.Cart;
import com.sparta.goatgam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserAndIsDeletedFalse(User user);
}
