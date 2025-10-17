package com.sparta.goatgam.domain.order.service;

import com.sparta.goatgam.domain.address.entity.Address;
import com.sparta.goatgam.domain.address.repository.AddressRepository;
import com.sparta.goatgam.domain.cart.entity.Cart;
import com.sparta.goatgam.domain.cart.entity.CartFood;
import com.sparta.goatgam.domain.cart.entity.CartFoodOption;
import com.sparta.goatgam.domain.cart.repository.CartRepository;
import com.sparta.goatgam.domain.order.dto.AdminOrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderDetailResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderSaveResponseDto;
import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.OrderFood;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sparta.goatgam.global.util.PageableUtils.makePageable;
import static com.sparta.goatgam.global.util.PageableUtils.order;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    public PagedModel<OrderSummaryResponseDto> getMyOrderSummary(int page, int size, User user) {
        Pageable pageable = makePageable(page, size, order(Sort.Direction.DESC, "createdAt"));

        Page<OrderSummaryResponseDto> orderSummaryList =
                orderRepository.findAllByUser(user, pageable).map(OrderSummaryResponseDto::new);

        return new PagedModel<>(orderSummaryList);
    }

    public PagedModel<AdminOrderSummaryResponseDto> getUserOrderSummary(Long userId, int page, int size) {
        Pageable pageable = makePageable(page, size,
                order(Sort.Direction.ASC, "userUserId"),
                order(Sort.Direction.DESC, "createdAt")
        );

        Page<AdminOrderSummaryResponseDto> orderSummaryList;

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ExceptionCode.USER_NOT_FOUND));

            orderSummaryList = orderRepository.findAllByUser(user, pageable).map(AdminOrderSummaryResponseDto::new);
        } else {
            orderSummaryList = orderRepository.findAll(pageable).map(AdminOrderSummaryResponseDto::new);
        }

        return new PagedModel<>(orderSummaryList);
    }

    public OrderDetailResponseDto getOrderDetail(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        return new OrderDetailResponseDto(order);
    }

    @Transactional
    public OrderSaveResponseDto addOrder(User user, String request) {
        Cart cart = cartRepository.findByUserAndIsDeletedFalse(user)
                .orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_EMPTY_CART));

        BigDecimal totalprice = new BigDecimal("0");
        List<OrderFood> foodsToAdd = new ArrayList<>();

        for (CartFood food : cart.getCartFoods()) {
            totalprice = totalprice.add(food.getFood().getFoodPrice());
            for (CartFoodOption option : food.getCartFoodOptions()) {
                totalprice = totalprice.add(option.getFoodOption().getSurcharge());
            }
            totalprice = totalprice.multiply(BigDecimal.valueOf(food.getQuantity()));
            foodsToAdd.add(OrderFood.fromCartFood(food));
        }

        Address address = addressRepository.findByUserUserIdAndIsDefaultTrue(user.getUserId()).orElseThrow(() ->
                new BusinessException(ExceptionCode.ORDER_DEFAULT_ADDRESS_NOT_FOUND)
        );

        Order order = new Order(
                address.getRoadAddress() + " " + address.getDetail(),
                totalprice,
                request,
                LocalDateTime.now(),
                StatusEnum.Request,
                user.getNickname(),
                user,
                cart.getRestaurant(),
                new ArrayList<>()
        );

        for (OrderFood of : foodsToAdd) {
            order.addOrderFood(of);
        }

        orderRepository.save(order);
        cart.delete(user);

        return new OrderSaveResponseDto(order.getOrderId(), "주문내역이 저장되었습니다.");
    }
}