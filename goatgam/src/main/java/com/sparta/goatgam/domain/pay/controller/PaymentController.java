package com.sparta.goatgam.domain.pay.controller;

import com.sparta.goatgam.domain.pay.dto.PaymentConfirmRequestDto;
import com.sparta.goatgam.domain.pay.dto.PaymentVerifyRequestDto;
import com.sparta.goatgam.domain.pay.service.PaymentService;
import com.sparta.goatgam.global.dto.MessageAndIdResponseDto;
import com.sparta.goatgam.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping("/cancel")
    public MessageAndIdResponseDto cancelPayment(@RequestParam UUID orderId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return paymentService.cancelPayment(orderId, userDetails.getUser());
    }
}
