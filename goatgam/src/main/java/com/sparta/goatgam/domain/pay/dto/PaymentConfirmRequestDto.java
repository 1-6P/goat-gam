package com.sparta.goatgam.domain.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentConfirmRequestDto {
    private String paymentKey;
    private UUID orderId;
    private BigDecimal amount;
}