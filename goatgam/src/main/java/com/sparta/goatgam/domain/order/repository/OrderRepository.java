package com.sparta.goatgam.domain.order.repository;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findAllByUser(User user, Pageable pageable);

    Page<Order> findByRestaurantAndStatus(Restaurant restaurant, StatusEnum status, Pageable pageable);

    Page<Order> findOrderByRestaurant(Restaurant restaurant, Pageable pageable);
}
