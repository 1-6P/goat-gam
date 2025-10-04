package com.sparta.goatgam.domain.follow.repository;

import com.sparta.goatgam.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    List<Follow> findAllByUser_UserId(Long userId);

    Follow findByUser_UserIdAndRestaurant_RestaurantId(Long userId, UUID restaurantId);
}
