package com.sparta.goatgam.domain.owner.service;

import com.sparta.goatgam.domain.order.dto.OrderDetailResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.restaurant.entity.Restaurant;
import com.sparta.goatgam.domain.restaurant.repository.RestaurantRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponseDto> getOrder(UUID restaurantId, User user, int page, int size, Sort.Direction direction, StatusEnum status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("식당을 찾을 수 없습니다."));
        if (!restaurant.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 식당의 권한이 없습니다.");
        }

        Pageable pageable = PageableUtils.makePageable(page, size,
                PageableUtils.order(direction, "createdAt"));

        Page<Order> orders;

        if (status == null) {
            orders = orderRepository.findOrderByRestaurant(restaurant, pageable);
        } else {
            orders = orderRepository.findByRestaurantAndStatus(restaurant, status, pageable);
        }

        return orders.map(OrderSummaryResponseDto::new);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID restaurantId,UUID orderId, User user) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("식당을 찾을 수 없습니다."));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        if(restaurant.getRestaurantId() != order.getRestaurant().getRestaurantId()){
            throw new RuntimeException("해당 식당의 주문이 아닙니다.");
        }

        if (!restaurant.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 식당의 권한이 없습니다.");
        }

        return  new OrderDetailResponseDto(order);
    }

    @Transactional
    public MessageAndIdResponseDto ChangeOrderStatus(UUID restaurantId, UUID orderId, User user, StatusEnum status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("식당을 찾을 수 없습니다."));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        if (!restaurant.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 식당의 권한이 없습니다.");
        }

        if (status == StatusEnum.Accept || status == StatusEnum.Reject) {
            if (!order.getStatus().equals(StatusEnum.Request)) {
                throw new RuntimeException("이미 처리된 주문입니다.");
            }
        }

        if (status == StatusEnum.Prepared) {
            if (!order.getStatus().equals(StatusEnum.Accept)) {
                throw new RuntimeException("먼저 주문을 수락해야합니다.");
            }
        }

        if (status == StatusEnum.OnDelivery) {
            if (!order.getStatus().equals(StatusEnum.Prepared)) {
                throw new RuntimeException("먼저 주문이 준비되어야합니다.");
            }
        }

        if (status == StatusEnum.Completed) {
            if (!order.getStatus().equals(StatusEnum.OnDelivery)) {
                throw new RuntimeException("아직 배송이 출발하지 않았습니다.");
            }
        }

        order.changeStatus(status);

        return new MessageAndIdResponseDto("success", orderId);
    }
}
