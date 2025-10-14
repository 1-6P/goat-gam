package com.sparta.goatgam.domain.pay.service;

import com.sparta.goatgam.domain.order.entity.Order;
import com.sparta.goatgam.domain.order.repository.OrderRepository;
import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.entity.Payment;
import com.sparta.goatgam.domain.pay.entity.PaymentMethodEnum;
import com.sparta.goatgam.domain.pay.entity.paymentStatusEnum;
import com.sparta.goatgam.domain.pay.repository.PaymentRepository;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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
}
