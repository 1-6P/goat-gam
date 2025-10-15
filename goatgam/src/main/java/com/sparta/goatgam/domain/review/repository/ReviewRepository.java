package com.sparta.goatgam.domain.review.repository;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review,UUID> {

    boolean existsByOrderAndStatus(Order order, Boolean status);

    Page<Review> findByRestaurant_RestaurantIdAndStatusTrue(UUID restaurantId, Pageable pageable);
}
