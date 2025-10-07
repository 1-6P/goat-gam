package com.sparta.goatgam.domain.order.service;

import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public PagedModel<OrderSummaryResponseDto> getMyOrderSummary(int page, int size, User user) {
        Pageable pageable = makePageable(page, size);

        Page<OrderSummaryResponseDto> orderSummaryList =
                orderRepository.findAllByUser(user, pageable).map(OrderSummaryResponseDto::new);

        return new PagedModel<>(orderSummaryList);
    }

    public PagedModel<OrderSummaryResponseDto> getUserOrderSummary(Long userId, int page, int size) {
        Pageable pageable = makePageable(page, size);

        Page<OrderSummaryResponseDto> orderSummaryList;

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. id=" + userId));

            orderSummaryList = orderRepository.findAllByUser(user, pageable).map(OrderSummaryResponseDto::new);
        } else {
            orderSummaryList = orderRepository.findAll(pageable).map(OrderSummaryResponseDto::new);
        }

        return new PagedModel<>(orderSummaryList);
    }

    private Pageable makePageable(int page, int size) {
        if (size != 10 && size != 30 && size != 50) size = 10;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(page, size, sort);
    }
}
