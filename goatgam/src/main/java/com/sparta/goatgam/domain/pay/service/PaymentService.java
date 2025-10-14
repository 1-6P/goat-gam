package com.sparta.goatgam.domain.pay.service;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.entity.Payment;
import com.sparta.goatgam.domain.pay.entity.PaymentMethodEnum;
import com.sparta.goatgam.domain.pay.entity.paymentStatusEnum;
import com.sparta.goatgam.domain.pay.repository.PaymentRepository;
import com.sparta.goatgam.domain.user.entity.User;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
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

    public MessageAndIdResponseDto varifyAmount(PaymentVerifyRequestDto paymentVerifyRequestDto) {
        Order order = orderRepository.findById(paymentVerifyRequestDto.getOrderId()).orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));

        if(order.getTotalPrice() != paymentVerifyRequestDto.getAmount()){
            throw new RuntimeException("금액이 일치하지 않습니다.");
        }
        return new MessageAndIdResponseDto("varify success", null);
    }

    @Transactional
    public MessageAndIdResponseDto confirmPayment(PaymentConfirmRequestDto dto) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));

        if (order.getTotalPrice() != dto.getAmount()) {
            throw new RuntimeException("금액이 일치하지 않습니다.");
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

    public MessageAndIdResponseDto cancelPayment(UUID orderId, User user) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("주문이 존재하지 않습니다."));
        Payment payment = paymentRepository.findPaymentByOrder(order).orElseThrow(() -> new RuntimeException("해당 주문의 결제가 존재하지 않습니다."));

        if(!order.getUser().equals(user)){
            throw new RuntimeException("해당 주문에 대한 권한이 없습니다.");
        }

        if(Duration.between(payment.getApprovedAt(), LocalDateTime.now()).toMinutes() > 5){
            throw new RuntimeException("취소 가능 시간이 지났습니다.");
        }

        payment.updateStatus(paymentStatusEnum.Canceled);
        paymentRepository.save(payment);

        return new MessageAndIdResponseDto("payment cancel success", orderId);
    }
}
