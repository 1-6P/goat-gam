package com.sparta.goatgam.domain.pay.dto;

import com.sparta.goatgam.domain.pay.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID paymentId;
    private String paymentKey;
    private String method;
    private String paymentStatus;
    private BigDecimal amount;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String cardLast4;
    private String cardIssuer;
    private String approveNo;
    private String receiptUrl;
    private UUID orderId;

    public PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.paymentKey = payment.getPaymentKey();
        this.method = payment.getMethod().name();
        this.paymentStatus = payment.getPaymentStatus().name();
        this.amount = payment.getAmount();
        this.requestedAt = payment.getRequestedAt();
        this.approvedAt = payment.getApprovedAt();
        this.cardLast4 = payment.getCardLast4();
        this.cardIssuer = payment.getCardIssuer();
        this.approveNo = payment.getApproveNo();
        this.receiptUrl = payment.getReceiptUrl();
        this.orderId = payment.getOrder().getOrderId();
    }
}
