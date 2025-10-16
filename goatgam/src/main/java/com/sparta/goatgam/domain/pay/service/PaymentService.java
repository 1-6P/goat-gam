package com.sparta.goatgam.domain.pay.service;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentResponseDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.entity.Payment;
import com.sparta.goatgam.domain.pay.entity.PaymentMethodEnum;
import com.sparta.goatgam.domain.pay.entity.paymentStatusEnum;
import com.sparta.goatgam.domain.pay.repository.PaymentRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.exception.BusinessException;
import com.sparta.goatgam.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.sparta.goatgam.global.util.PageableUtils.makePageable;
import static com.sparta.goatgam.global.util.PageableUtils.order;

@Service
@RequiredArgsConstructor

@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public MessageAndIdResponseDto verifyAmount(PaymentVerifyRequestDto paymentVerifyRequestDto, User user) {
        Order order = orderRepository.findById(paymentVerifyRequestDto.getOrderId()).orElseThrow(()
                -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.INVALID_USER);
        }

        if (order.getTotalPrice().intValue() != paymentVerifyRequestDto.getAmount().intValue()) {
            throw new BusinessException(ExceptionCode.PG_AMOUNT_INCORRECT);
        }
        return new MessageAndIdResponseDto("verify success", null);
    }

    @Transactional
    public MessageAndIdResponseDto confirmPayment(PaymentConfirmRequestDto dto, User user) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.INVALID_USER);
        }

        if (order.getTotalPrice().intValue() != dto.getAmount().intValue()) {
            throw new BusinessException(ExceptionCode.PG_AMOUNT_INCORRECT);
        }

        Payment payment = Payment.builder()
                .paymentKey("mock_" + UUID.randomUUID())  // PG사가 주는 key 대신 직접 생성
                .method(PaymentMethodEnum.Card)
                .paymentStatus(paymentStatusEnum.Done)    // 항상 성공으로 처리
                .amount(dto.getAmount())
                .requestedAt(LocalDateTime.now())
                .approvedAt(LocalDateTime.now())
                .cardLast4("1234")
                .cardIssuer("MockBank")
                .approveNo("MOCK123456")
                .receiptUrl("https://mock-receipt-url.local/test") // 임의 값
                .order(order)
                .build();

        paymentRepository.save(payment);

        return new MessageAndIdResponseDto("mock payment success", payment.getPaymentId());
    }

    @Transactional
    public MessageAndIdResponseDto cancelPayment(UUID orderId, User user) {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));
        Payment payment = paymentRepository.findPaymentByOrder(order).orElseThrow(()
                -> new BusinessException(ExceptionCode.PG_NOT_FOUND));

        if (!order.getUser().equals(user)) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_ORDER);
        }

        if (Duration.between(payment.getApprovedAt(), LocalDateTime.now()).toMinutes() > 5) {
            throw new BusinessException(ExceptionCode.PG_CANCEL_TIMEOUT);
        }

        payment.updateStatus(paymentStatusEnum.Canceled);
        order.changeStatus(StatusEnum.Cancel);

        return new MessageAndIdResponseDto("payment cancel success", orderId);
    }

    @Transactional
    public MessageAndIdResponseDto refundPayment(PaymentVerifyRequestDto dto, User user) {
        Order order = orderRepository.findById(dto.getOrderId()).orElseThrow(()
                -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));
        Payment payment = paymentRepository.findPaymentByOrder(order).orElseThrow(()
                -> new BusinessException(ExceptionCode.PG_NOT_FOUND));

        if (!order.getRestaurant().getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_ORDER_REFUND);
        }

        if (payment.getPaymentStatus() != paymentStatusEnum.Done) {
            throw new BusinessException(ExceptionCode.PG_CANT_REFUND);
        }

        if (dto.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new BusinessException(ExceptionCode.PG_REFUND_AMOUNT_INCORRECT);
        }

        payment.updateStatus(paymentStatusEnum.Refunded);
        order.changeStatus(StatusEnum.Refund);

        return new MessageAndIdResponseDto("refund success", dto.getOrderId());
    }

    public PagedModel<PaymentResponseDto> getPaymentList(int page, int size, String direction, User user) {
        Pageable pageable = makePageable(
                page,
                size,
                order((direction.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), "requestedAt")
        );

        Page<Payment> payments = paymentRepository.findAllByOrderUser(user, pageable);

        return new PagedModel<>(payments.map(PaymentResponseDto::new));
    }
}
