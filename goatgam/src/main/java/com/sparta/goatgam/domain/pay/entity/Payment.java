package com.sparta.goatgam.domain.pay.entity;

import com.sparta.goatgam.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", nullable = false, updatable = false)
    private UUID paymentId;

    // 토스 결제 키
    @Column(name = "payment_key", nullable = false, length = 100)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 10)
    private PaymentMethodEnum method;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, updatable = false, length = 10)
    private paymentStatusEnum paymentStatus;

    @Column(name = "amount", nullable = false, updatable = false)
    private int amount;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at", updatable = false)
    private LocalDateTime approvedAt;

    // 카드번호 마스킹 끝자리
    @Column(name = "card_last4", updatable = false, length = 10)
    private String cardLast4;

    // 카드 발급사 코드
    @Column(name = "card_issuer", updatable = false, length = 50)
    private String cardIssuer;

    // 카드 승인번호
    @Column(name = "approve_no", updatable = false, length = 20)
    private String approveNo;

    @Column(name = "receipt_url", columnDefinition = "TEXT", updatable = false, length = 10)
    private String receiptUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
