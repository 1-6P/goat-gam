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
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import com.sparta.goatgam.global.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerService {
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public PagedModel<OrderSummaryResponseDto> getOrder(UUID restaurantId, User user, int page, int size, Sort.Direction direction, StatusEnum status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new BusinessException(ExceptionCode.RESTAURANT_NOT_FOUND));
        if (!restaurant.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_RESTAURANT);
        }

        Pageable pageable = PageableUtils.makePageable(page, size,
                PageableUtils.order(direction, "createdAt"));

        Page<Order> orders;

        if (status == null) {
            orders = orderRepository.findOrderByRestaurant(restaurant, pageable);
        } else {
            orders = orderRepository.findByRestaurantAndStatus(restaurant, status, pageable);
        }
        return new PagedModel<>(orders.map(OrderSummaryResponseDto::new));
    }

    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID restaurantId,UUID orderId, User user) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new BusinessException(ExceptionCode.RESTAURANT_NOT_FOUND));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if(restaurant.getRestaurantId() != order.getRestaurant().getRestaurantId()){
            throw new BusinessException(ExceptionCode.FOOD_INPUT_ERROR);
        }

        if (!restaurant.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_RESTAURANT);
        }

        return  new OrderDetailResponseDto(order);
    }

    @Transactional
    public MessageAndIdResponseDto ChangeOrderStatus(UUID restaurantId, UUID orderId, User user, StatusEnum status) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new BusinessException(ExceptionCode.RESTAURANT_NOT_FOUND));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if (!restaurant.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_RESTAURANT);
        }

        if (status == StatusEnum.Accept || status == StatusEnum.Reject) {
            if (!order.getStatus().equals(StatusEnum.Request)) {
                throw new BusinessException(ExceptionCode.ORDER_ALREADY_PROCESSED);
            }
        }

        if (status == StatusEnum.Prepared) {
            if (!order.getStatus().equals(StatusEnum.Accept)) {
                throw new BusinessException(ExceptionCode.ORDER_ACCEPT_REQUIRED);
            }
        }

        if (status == StatusEnum.OnDelivery) {
            if (!order.getStatus().equals(StatusEnum.Prepared)) {
                throw new BusinessException(ExceptionCode.ORDER_PREPARED_REQUIRED);
            }
        }

        if (status == StatusEnum.Completed) {
            if (!order.getStatus().equals(StatusEnum.OnDelivery)) {
                throw new BusinessException(ExceptionCode.ORDER_DELIVERY_REQUIRED);
            }
        }

        order.changeStatus(status);

        return new MessageAndIdResponseDto("success", orderId);
    }
}
