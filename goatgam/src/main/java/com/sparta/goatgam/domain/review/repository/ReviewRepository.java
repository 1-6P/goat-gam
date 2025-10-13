package com.sparta.goatgam.domain.review.repository;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review,UUID> {

    List<Review> findAllByRestaurant_RestaurantId(UUID restaurantId);

    boolean existsByOrderAndStatus(Order order, Boolean status);
}
