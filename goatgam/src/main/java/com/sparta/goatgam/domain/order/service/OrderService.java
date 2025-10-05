package com.sparta.goatgam.domain.order.service;

import com.sparta.goatgam.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public PagedModel<OrderSummaryResponseDto> getMyOrderSummary(int page, int size, User user) {
        if (size != 10 && size != 30 && size != 50) size = 10;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<OrderSummaryResponseDto> orderSummaryList =
                orderRepository.findAllByUser(user, pageable).map(OrderSummaryResponseDto::new);

        return new PagedModel<>(orderSummaryList);
    }
}
