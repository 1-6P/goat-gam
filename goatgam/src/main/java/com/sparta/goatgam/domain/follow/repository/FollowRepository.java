package com.sparta.goatgam.domain.follow.repository;

import com.sparta.goatgam.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    List<Follow> findAllByUser_UserId(Long userId);

    Follow findByUser_UserIdAndRestaurant_RestaurantId(Long userId, UUID restaurantId);
}
