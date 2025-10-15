package com.sparta.goatgam.domain.pay.service;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.entity.StatusEnum;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public MessageAndIdResponseDto varifyAmount(PaymentVerifyRequestDto paymentVerifyRequestDto, User user) {
        Order order = orderRepository.findById(paymentVerifyRequestDto.getOrderId()).orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if(!order.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.INVALID_USER);
        }

        if(order.getTotalPrice() != paymentVerifyRequestDto.getAmount()){
            throw new BusinessException(ExceptionCode.PG_AMOUNT_INCORRECT);
        }
        return new MessageAndIdResponseDto("varify success", null);
    }

    @Transactional
    public MessageAndIdResponseDto confirmPayment(PaymentConfirmRequestDto dto, User user) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));

        if(!order.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.INVALID_USER);
        }

        if (order.getTotalPrice() != dto.getAmount()) {
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
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));
        Payment payment = paymentRepository.findPaymentByOrder(order).orElseThrow(() -> new BusinessException(ExceptionCode.PG_NOT_FOUND));

        if(!order.getUser().equals(user)){
            throw new BusinessException(ExceptionCode.FORBIDDEN_ORDER);
        }

        if(Duration.between(payment.getApprovedAt(), LocalDateTime.now()).toMinutes() > 5){
            throw new BusinessException(ExceptionCode.PG_CANCEL_TIMEOUT);
        }

        payment.updateStatus(paymentStatusEnum.Canceled);
        order.changeStatus(StatusEnum.Cancel);

        return new MessageAndIdResponseDto("payment cancel success", orderId);
    }

    @Transactional
    public MessageAndIdResponseDto refundPayment(PaymentVerifyRequestDto dto, User user) {
        Order order = orderRepository.findById(dto.getOrderId()).orElseThrow(() -> new BusinessException(ExceptionCode.ORDER_NOT_FOUND));
        Payment payment = paymentRepository.findPaymentByOrder(order).orElseThrow(() -> new BusinessException(ExceptionCode.PG_NOT_FOUND));

        if(!order.getRestaurant().getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ExceptionCode.FORBIDDEN_ORDER_REFUND);
        }

        if(payment.getPaymentStatus() != paymentStatusEnum.Done){
            throw new BusinessException(ExceptionCode.PG_CANT_REFUND);
        }

        if(dto.getAmount() > payment.getAmount()){
            throw new BusinessException(ExceptionCode.PG_REFUND_AMOUNT_INCORRECT);
        }

        payment.updateStatus(paymentStatusEnum.Refunded);
        order.changeStatus(StatusEnum.Refund);

        return new MessageAndIdResponseDto("refund success", dto.getOrderId());
    }
}
