package com.sparta.goatgam.domain.pay.controller;

import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.service.PaymentService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/verify-amount")
    public MessageAndIdResponseDto verifyAmount(@RequestBody PaymentVerifyRequestDto paymentVerifyRequestDto){
        return paymentService.varifyAmount(paymentVerifyRequestDto);
    }

    @PostMapping("/confirm")
    public MessageAndIdResponseDto confirmPayment(@RequestBody PaymentConfirmRequestDto paymentConfirmRequestDto){
        return paymentService.confirmPayment(paymentConfirmRequestDto);
    }
}
